package com.goatlerbon.aim.client.service.impl;

import com.goatlerbon.aim.client.config.AppConfiguration;
import com.goatlerbon.aim.client.service.MsgLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AsyncMsgLogger implements MsgLogger{

    @Autowired
    AppConfiguration appConfiguration;

    private final static Logger LOGGER = LoggerFactory.getLogger(AsyncMsgLogger.class);

    /**
     * 默认缓冲区大小
     */
    private static final int DEFAULT_QUEUE_SIZE = 16;

    private BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);

    private volatile boolean started = false;

    private Worker worker = new Worker();
    @Override
    public void log(String msg) {
        //开始消费
        startMsgLogger();
        try {
            // TODO: 2019/1/6 消息堆满是否阻塞线程？
            blockingQueue.put(msg);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }
    }

    @Override
    public void stop() {
        started = false;
//        关闭则将 worker  的 中断标志打开
        worker.interrupt();
    }

    @Override
    public String query(String key) {
        StringBuffer sb = new StringBuffer();

        Path path = Paths.get(appConfiguration.getMsgLoggerPath() + appConfiguration.getUserName() + "/");
        try {
//            先获得stream流
            Stream<Path> list = Files.list(path);
            List<Path> collect = list.collect(Collectors.toList());
            for(Path file : collect){
                List<String > strings = Files.readAllLines(file);
                for(String msg : strings){
                    if(msg.trim().contains(key)){
                        sb.append(msg).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.info("IOException", e);
        }
        return sb.toString().replace(key, "\033[31;4m" + key + "\033[0m");
    }

    private class Worker extends Thread{
        @Override
        public void run() {
            while (started){
                try {
                    String msg = blockingQueue.take();
                    writeLog(msg);
                }catch (InterruptedException e){
                    break;
                }
            }
        }
    }

    private void writeLog(String msg){
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        String dir = appConfiguration.getMsgLoggerPath() + appConfiguration.getUserName() + "/";
        String fileName = dir + year + month + day + ".log";

        Path  file = Paths.get(fileName);
        boolean exists = Files.exists(Paths.get(dir), LinkOption.NOFOLLOW_LINKS);
        try {
            if(!exists){
                Files.createDirectories(Paths.get(dir));
            }

            List<String > lines = Arrays.asList(msg);

            Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMsgLogger(){
        if(started){
            return;
        }

        /**
         * 只要当前JVM实例中尚存在任何一个非守护线程没有结束，守护线程就全部工作；
         * 只有当最后一个非守护线程结束时，守护线程随着JVM一同结束工作。
         * Daemon的作用是为其他线程的运行提供便利服务，守护线程最典型的应用就是 GC (垃圾回收器)，
         * 它就是一个很称职的守护者
         */
//        设置为守护线程
        worker.setDaemon(true);
        worker.setName("AsyncMsgLogger-Worker");
        started = true;
        worker.start();
    }
}
