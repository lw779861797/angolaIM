package com.goatlerbon.aim.client.service;

/**
 * 回显服务类
 */
public interface EchoService {

    /**
     * 回显到终端
     * @param msg
     * @param replace
     */
    public void echo(String msg,Object ...replace);
}
