package com.goatlerbon.aim.client.handle;

import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.ReConnectManager;
import com.goatlerbon.aim.client.service.ShutDownMsg;
import com.goatlerbon.aim.client.service.impl.EchoServiceImpl;
import com.goatlerbon.aim.client.util.SpringBeanFactory;
import com.goatlerbon.aim.common.constant.Constants;
import com.goatlerbon.aim.common.protocol.AIMRequestProto;
import com.goatlerbon.aim.common.protocol.AIMResponseProto;
import com.goatlerbon.aim.common.util.NettyAttrUtil;
import com.vdurmont.emoji.EmojiParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@ChannelHandler.Sharable
public class AIMClientHandle extends SimpleChannelInboundHandler<AIMResponseProto.AIMResProtocol> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AIMClientHandle.class);

    private ShutDownMsg shutDownMsg;

    private ScheduledExecutorService scheduledExecutorService;

    private ReConnectManager reConnectManager;

    private EchoService echoService;

    private ThreadPoolExecutor threadPoolExecutor;

    private MsgHandleCaller caller;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        如果该事件是心跳事件 就得发送心跳了
//        也就是 客户端如果一段时间没有发心跳包 就得发心跳包
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(idleStateEvent.state() == IdleState.WRITER_IDLE){
                AIMRequestProto.AIMReqProtocol heartBeat = SpringBeanFactory.getBean("heartBeat",
                        AIMRequestProto.AIMReqProtocol.class);
                ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        LOGGER.error("IO error,close Channel");
                        future.channel().close();
                    }
                }) ;
            }
        }
        super.userEventTriggered(ctx,evt);
    }

    /**
     * 连接成功时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("aim server connect success!");
    }

    /**
     * 客户端和服务器断开时 触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(shutDownMsg == null){
            shutDownMsg = SpringBeanFactory.getBean(ShutDownMsg.class);
        }

//        用户主动退出则不重连
        if(shutDownMsg.checkStatus()){
            return ;
        }

        if(scheduledExecutorService == null){
            scheduledExecutorService = SpringBeanFactory.getBean("scheduledTask",ScheduledExecutorService.class);
            reConnectManager = SpringBeanFactory.getBean(ReConnectManager.class);
        }
        LOGGER.info("客户端断开了，重新连接！");
        reConnectManager.reConnect(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AIMResponseProto.AIMResProtocol msg) throws Exception {
        if(echoService == null){
            echoService = SpringBeanFactory.getBean(EchoServiceImpl.class);
        }

        //心跳更新时间
        if (msg.getType() == Constants.CommandType.PING){
            //LOGGER.info("收到服务端心跳！！！");
            NettyAttrUtil.updateReaderTime(ctx.channel(),System.currentTimeMillis());
        }

        if(msg.getType() != Constants.CommandType.PING){
            //回调消息
            callBackMsg(msg.getResMsg());

            //将消息中的 emoji 表情格式化为 Unicode 编码以便在终端可以显示
            String response = EmojiParser.parseToUnicode(msg.getResMsg());
            echoService.echo(response);
        }
    }

    /**
     * 回调消息
     * @param msg
     */
    private void callBackMsg(String msg) {
        threadPoolExecutor = SpringBeanFactory.getBean("callBackThreadPool",ThreadPoolExecutor.class);
//        利用线程池将消息存入日志中
        threadPoolExecutor.execute(() -> {
            caller = SpringBeanFactory.getBean(MsgHandleCaller.class) ;
            caller.getMsgHandleListener().handle(msg);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常时断开连接
        cause.printStackTrace() ;
        ctx.close() ;
    }
}
