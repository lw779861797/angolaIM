package com.goatlerbon.aim.common.data.construct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 环形队列，可以用来延迟任务。
 * https://www.cnblogs.com/hohoa/p/7739271.html 这个 是原理实现
 */
public class RingBufferWheel {

    private Logger logger = LoggerFactory.getLogger(RingBufferWheel.class);

    /**
     * 默认环形缓冲区大小
     */
    private static final int STATIC_RING_SIZE = 64;

    //环形缓冲区
    private Object[] ringBuffer;

    //缓冲区大小
    private int bufferSize;

    /**
     * 业务线程池
     */
    private ExecutorService executorService;

    private volatile int size = 0;

    /***
     * 任务停止标志
     * 只需要 保证 可见性 因为不存在 对 停止标志的并发操作 不需要保证原子性
     */
    private volatile boolean stop = false;

    /**
     * 任务开始标志
     * 既需要保证原子性 又需要保证可见性
     */
    private volatile AtomicBoolean start = new AtomicBoolean(false);

    /**
     * 总计时次数
     */
    private AtomicInteger tick = new AtomicInteger();

    /**
     * 控制线程安全锁
     */
    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    //用于为任务生成ID
    private AtomicInteger taskId = new AtomicInteger();

    private Map<Integer, Task> taskMap = new ConcurrentHashMap<>(16);

    /**
     * 按默认大小创建新的延迟任务环缓冲区
     *
     * @param executorService the business thread pool
     */
    public RingBufferWheel(ExecutorService executorService) {
        this.executorService = executorService;
        this.bufferSize = STATIC_RING_SIZE;
        this.ringBuffer = new Object[bufferSize];
    }

    /**
     * 按自定义缓冲区大小创建新的延迟任务环缓冲区
     *
     * @param executorService the business thread pool
     * @param bufferSize      custom buffer size
     */
    public RingBufferWheel(ExecutorService executorService, int bufferSize) {
        this(executorService);

        //判断 输入的数组大小是否符合规范 也就是是否是二的倍数
        if (!powerOf2(bufferSize)) {
            throw new RuntimeException("bufferSize=[" + bufferSize + "] must be a power of 2");
        }
        this.bufferSize = bufferSize;
        this.ringBuffer = new Object[bufferSize];
    }

    /**
     * 将任务添加到环形缓冲区（线程安全）
     * @param task business task extends {@link Task}
     * @return
     */
    public int addTask(Task task){
//        获取该任务的延迟时间
        int key = task.getKey();
        int id;

        try {
            lock.lock();
//            这个index 就相当于 相对于当前时间tick 需要走几轮数组大小的时间 才刚好延迟时间用完的数组下标索引
//            环形队列的实现其实就是一秒走一个缓冲区 也就是 一个 数组格子的大小 ，一直循环数组大小
//            直到数组中每个元素都已经遍历完成
            int index = mod(key,bufferSize);
            task.setIndex(index);
            Set<Task> tasks = get(index);

            /**
             * cycleNum 的意思 就是 该元素什么时候需要被执行
             * 相当于要遍历多少次数组
             * 如果延迟时间为100 数组大小为30(秒), 则该元素 需要遍历 3 次 数组  + 10的移动格子
             */
            int cycleNum = cycleNum(key, bufferSize);
            if(tasks != null){
                task.setCycleNum(cycleNum);
                tasks.add(task);
            }else {
                task.setIndex(index);
                task.setCycleNum(cycleNum);
                Set<Task> sets = new HashSet<>();
                sets.add(task);
                put(key, sets);
            }
            id = taskId.incrementAndGet();
            task.setTaskId(id);
            taskMap.put(id,task);
            size++;
        }finally {
            lock.unlock();
        }

        start();

        return id;
    }

    /**
     * 启动后台线程到消费轮计时器，它将一直运行，直到您调用方法{@link#stop}
     */
    public void start(){
        if(!start.get()){
            if(start.compareAndSet(start.get(),true)){
                logger.info("Delay task is starting");
                Thread job = new Thread(new TriggerJob());
                job.setName("consumer RingBuffer thread");
                job.start();
//                将开始标识设置为true
                start.set(true);
            }
        }
    }

    /**
     * 按任务ID取消任务
     * @param id id 具有唯一性
     * @return
     */
    public boolean cancel(int id){
        boolean flag = false;
        Set<Task> tempTask = new HashSet<>();

        try {
            lock.lock();
            Task task = taskMap.get(id);
            if(task == null){
                return false;
            }

            Set<Task> tasks = get(task.getIndex());

            for (Task tk : tasks){
                //这边 删除 的是相同延迟时间 并且 相同 循环次数的 元素 也就是相等元素 ，是否有可能是重复添加
                if (tk.getKey() == task.getKey() && tk.getCycleNum() == task.getCycleNum()) {
                    size--;
                    flag = true;
                    taskMap.remove(id);
                } else {
                    tempTask.add(tk);
                }
            }
            //更新
            ringBuffer[task.getIndex()] = tempTask;
        }finally {
            lock.unlock();
        }
        return flag;
    }

    /**
     * 停止 消费 环形缓冲 线程
     *
     * @param force True将强制关闭使用者线程并丢弃所有挂起的任务，否则使用者线程将等待所有任务完成后再关闭。
     */
    public void stop(boolean force){
        if(force){
            logger.info("Delay task is forced stop");
            stop = true;
            //shutdown 不再接受新任务了,但是它即不会强行终止正在执行的任务，也不会取消已经提交的任务。
            //shutdownNow 对于尚未执行的任务，全部取消掉,对于正在执行的任务，发出interrupt()。
            executorService.shutdownNow();
        }else {
            logger.info("Delay task is stopping");
            if (taskSize() > 0) {
                try {
                    lock.lock();
//                    让 执行stop 的这个线程等待 是为了 让所有的延迟任务 执行完 如果size = 0 时 这个线程就会被唤醒了
                    condition.await();
                    stop = true;
                } catch (InterruptedException e) {
                    logger.error("InterruptedException", e);
                } finally {
                    lock.unlock();
                }
            }
            executorService.shutdown();
        }
    }

    /**
     * 线程安全的
     *
     * @return the size of ring buffer
     */
    public int taskSize() {
        return size;
    }

    /**
     * 相同方法 {@link #taskSize}
     * @return
     */
    public int taskMapSize(){
        return taskMap.size();
    }

    /**
     * 将元素放入环形数组中的集合
     * @param key
     * @param tasks
     */
    private void put(int key,Set<Task> tasks){
        //类似于计算出 hash值
        int index = mod(key,bufferSize);
        ringBuffer[index] = tasks;
    }

    private int cycleNum(int target, int mod) {
        //equals target/mod
        return target >> Integer.bitCount(mod - 1);
    }
    private Set<Task> get(int index) {
        return (Set<Task>) ringBuffer[index];
    }

    /**
     * 计算 元素在 数组中的索引
     * @param target 获取该任务的延迟时间
     * @param mod 缓冲区大小
     * @return
     */
    private int mod(int target, int mod) {
        // equals target % mod
        //tick 为总的计时时间
        target = target + tick.get();
        return target & (mod - 1);
    }

    /**
     * 判断 输入的数组大小是否符合规范 也就是是否是二的倍数
     * @param target
     * @return
     */
    private boolean powerOf2(int target) {
        if (target < 0) {
            return false;
        }
        int value = target & (target - 1);
        if (value != 0) {
            return false;
        }

        return true;
    }

    /**
     * 删除并获取任务列表。
     * @param index 缓冲数组索引
     * @return
     */
    private Set<Task> remove(int index) {
        Set<Task> tempTask = new HashSet<>();
        Set<Task> result = new HashSet<>();

        Set<Task> tasks = (Set<Task>) ringBuffer[index];
        if(tasks == null){
            return result;
        }

        for(Task task : tasks){
//            等于0 说明 该任务的延迟时间已经到了
            if(task.getCycleNum() == 0){
                result.add(task);

                size2Notify();
            }else {
//                如果不为0 则说明该次 循环还没到 延迟时间 继续 往后扫描
//                减少1循环次数并更新原始数据
                task.setCycleNum(task.getCycleNum() - 1);
                tempTask.add(task);
            }

            //这边 需要 删除的原因是
//            删除任务，释放内存。
            taskMap.remove(task.getTaskId());
        }

        ringBuffer[index ] = tempTask;
        return result;
    }

    private void size2Notify() {
        try {
            lock.lock();
            size--;
            if(size == 0){
                condition.signal();
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 用于实现业务的抽象类。
     */
    public abstract static class Task extends Thread{
        //在数组中的索引
        private int index;

        /**
         * 需要遍历的圈数
         */
        private int cycleNum;

        //延迟时间
        private int key;

//        每个任务的唯一ID
        private int taskId;

        @Override
        public void run() {
        }

        public int getKey() {
            return key;
        }

        /**
         *
         * @param key Delay time(seconds)
         */
        public void setKey(int key) {
            this.key = key;
        }

        public int getCycleNum() {
            return cycleNum;
        }

        private void setCycleNum(int cycleNum) {
            this.cycleNum = cycleNum;
        }

        public int getIndex() {
            return index;
        }

        private void setIndex(int index) {
            this.index = index;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }
    }

    private class TriggerJob implements Runnable{

        @Override
        public void run() {
            int index = 0;
            while (!stop){
                try {
                    Set<Task> tasks = remove(index);
                    for(Task task : tasks){
                        executorService.submit(task);
                    }

                    index = (index + 1) % (bufferSize - 1);

//                    总时间 记录 增加1
                    tick.incrementAndGet();

//                    隔一秒走一格
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    logger.error("Exception", e);
                }
            }
            logger.info("Delay task has stopped");
        }
    }


}
