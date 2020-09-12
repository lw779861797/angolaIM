package com.goatlerbon.aim.server.kit;

import com.goatlerbon.aim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳服务类
 */
public class ServerHeartBeatHandlerImpl implements HeartBeatHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHeartBeatHandlerImpl.class);

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {

    }
}
