package com.goatlerbon.aim.server.handle;

import com.goatlerbon.aim.common.protocol.AIMRequestProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AIMServerHandler extends SimpleChannelInboundHandler<AIMRequestProto.AIMReqProtocol> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AIMServerHandler.class);

    /**
     * 当前channel不活跃的时候，也就是当前channel到了它生命周期末触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 用户事件触发的时候 触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    /**
     *	当前channel从远端读取到数据
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AIMRequestProto.AIMReqProtocol msg) throws Exception {
        LOGGER.info("received msg=[{}]", msg.toString());

    }

    /**
     * 报异常的时候触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
