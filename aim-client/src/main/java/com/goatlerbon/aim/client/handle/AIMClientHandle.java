package com.goatlerbon.aim.client.handle;

import com.goatlerbon.aim.common.protocol.AIMResponseProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AIMClientHandle extends SimpleChannelInboundHandler<AIMResponseProto.AIMResProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AIMResponseProto.AIMResProtocol aimResProtocol) throws Exception {

    }
}
