package com.goatlerbon.aim.client.service;

/**
 * 消息日志
 */
public interface MsgLogger {
    /**
     * 异步写入消息
     * @param msg
     */
    void log(String msg);

    /**
     * 停止写入
     */
    void stop();

    String query(String s);
}
