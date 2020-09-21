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

    /**
     * 查询聊天记录
     * @param s
     * @return
     */
    String query(String s);
}
