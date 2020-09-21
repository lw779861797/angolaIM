package com.goatlerbon.aim.client.service;

import com.goatlerbon.aim.client.thread.ReConnectJob;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class ReConnectManager {
    private ScheduledExecutorService scheduledExecutorService;

    public void reConnect(ChannelHandlerContext ctx){
        buildExecutor();
        /**
         *  scheduleAtFixedRate 表示每间隔一段时间定时执行任务。
         *  scheduleAtFixedRate是以上一次任务的开始时间为间隔的，
         *  并且当任务执行时间大于设置的间隔时间时，
         *  真正间隔的时间由任务执行时间为准！
         *  0 代表 没有 初始的延迟时间
         *  10 代表 每隔 10 秒执行一次
         */
        scheduledExecutorService.scheduleAtFixedRate(new ReConnectJob(ctx),0,10, TimeUnit.SECONDS);
    }

    /**
     * 如果重新连接成功，关闭重新连接作业。
     */
    public void reConnectSuccess() {
        scheduledExecutorService.shutdown();
    }

    /**
     * 构建一个定时的线程池
     */
    public ScheduledExecutorService buildExecutor(){
        if(scheduledExecutorService == null || scheduledExecutorService.isShutdown()){
            ThreadFactory factory = new ThreadFactoryBuilder()
                    .setNameFormat("reConnect-job-%d")
                    .setDaemon(true)
                    .build();
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1,factory);
            return scheduledExecutorService;
        }else {
            return scheduledExecutorService;
        }
    }
}
