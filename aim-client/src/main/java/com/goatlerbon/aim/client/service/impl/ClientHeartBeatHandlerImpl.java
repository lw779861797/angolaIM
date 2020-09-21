package com.goatlerbon.aim.client.service.impl;

import com.goatlerbon.aim.client.client.AIMClient;
import com.goatlerbon.aim.client.thread.ContextHolder;
import com.goatlerbon.aim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 客户端重连
 */
@Service
public class ClientHeartBeatHandlerImpl implements HeartBeatHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientHeartBeatHandlerImpl.class);

    @Autowired
    AIMClient aimClient;

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        //重连
        ContextHolder.setReconnect(true);

//        重新连接
        aimClient.reconnect();
    }
}
