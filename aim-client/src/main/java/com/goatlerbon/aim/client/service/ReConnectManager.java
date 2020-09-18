package com.goatlerbon.aim.client.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

@Component
public class ReConnectManager {
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * 如果重新连接成功，关闭重新连接作业。
     */
    public void reConnectSuccess() {
        scheduledExecutorService.shutdown();
    }
}
