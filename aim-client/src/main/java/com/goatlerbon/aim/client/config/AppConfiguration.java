package com.goatlerbon.aim.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *服务器配置
 */
@Component
public class AppConfiguration {
    @Value("${aim.user.id}")
    private Long userId;

    @Value("${aim.user.userName}")
    private String userName;

    @Value("${aim.msg.logger.path}")
    private String msgLoggerPath ;

    @Value("${aim.clear.route.request.url}")
    private String clearRouteUrl ;

    @Value("${aim.heartbeat.time}")
    private long heartBeatTime ;

    @Value("${aim.reconnect.count}")
    private int errorCount ;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsgLoggerPath() {
        return msgLoggerPath;
    }

    public void setMsgLoggerPath(String msgLoggerPath) {
        this.msgLoggerPath = msgLoggerPath;
    }

    public String getClearRouteUrl() {
        return clearRouteUrl;
    }

    public void setClearRouteUrl(String clearRouteUrl) {
        this.clearRouteUrl = clearRouteUrl;
    }

    public long getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
