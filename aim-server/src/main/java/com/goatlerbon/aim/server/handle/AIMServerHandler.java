package com.goatlerbon.aim.server.handle;

import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.kit.HeartBeatHandler;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.protocol.AIMRequestProto;
import com.goatlerbon.aim.common.util.NettyAttrUtil;
import com.goatlerbon.aim.server.kit.RouteHandler;
import com.goatlerbon.aim.server.kit.ServerHeartBeatHandlerImpl;
import com.goatlerbon.aim.server.util.SessionSocketHolder;
import com.goatlerbon.aim.server.util.SpringBeanFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.goatlerbon.aim.common.constant.Constants;

public class AIMServerHandler extends SimpleChannelInboundHandler<AIMRequestProto.AIMReqProtocol> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AIMServerHandler.class);

    /**
     * 当前channel不活跃的时候，也就是当前channel到了它生命周期末触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //可能出现业务判断离线后再次触发 channelInactive
        AIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null){
            LOGGER.warn("[{}] trigger channelInactive offline!",userInfo.getUserName());

            //Clear route info and offline.
            RouteHandler routeHandler = SpringBeanFactory.getBean(RouteHandler.class);
            routeHandler.userOffLine(userInfo,(NioSocketChannel) ctx.channel());

            ctx.channel().close();
        }
    }

    /**
     * 用户事件触发的时候 触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        该事件通过 IdleStateHandler 触发
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                LOGGER.info("定时检测客户端是否存活");

                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatHandlerImpl.class) ;
                heartBeatHandler.process(ctx) ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     *	当前channel从远端读取到数据触发
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AIMRequestProto.AIMReqProtocol msg) throws Exception {
        LOGGER.info("received msg=[{}]", msg.toString());


        //此消息为登入操作
        if(msg.getType() == Constants.CommandType.LOGIN){
//            保存客户端用户 与 channel之间的关系
            SessionSocketHolder.put(msg.getRequestId(), (NioSocketChannel) ctx.channel());
            SessionSocketHolder.saveSession(msg.getRequestId(),msg.getReqMsg());
            LOGGER.info("client [{}] online success!!", msg.getReqMsg());
        }

        //心跳监测  更新时间
        if(msg.getType() == Constants.CommandType.PING){
            NettyAttrUtil.updateReaderTime(ctx.channel(),System.currentTimeMillis());
            AIMRequestProto.AIMReqProtocol heartBeat = SpringBeanFactory.getBean("heartBeat",
                    AIMRequestProto.AIMReqProtocol.class);
            ctx.writeAndFlush(heartBeat).addListener((ChannelFutureListener) future -> {
                if(!future.isSuccess()){
                    LOGGER.error("IO error,close Channel");
                    future.channel().close();
                }
            });
        }
    }

    /**
     * 报异常的时候触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(AIMException.isResetByPeer(cause.getMessage())){
            return ;
        }
        LOGGER.error(cause.getMessage(), cause);
    }
}
