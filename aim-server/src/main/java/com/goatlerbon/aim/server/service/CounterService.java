package com.goatlerbon.aim.server.service;

/**
 * TODO: 对于 用户消息的统计等等
 */
public interface CounterService {
    void increment(String var1);

    void decrement(String var1);

    void reset(String var1);
}
