package com.goatlerbon.aim.client.client;

import com.goatlerbon.aim.client.config.AppConfiguration;
import com.goatlerbon.aim.client.init.AIMClientHandleInitializer;
import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.MsgHandle;
import com.goatlerbon.aim.client.service.ReConnectManager;
import com.goatlerbon.aim.client.service.RouteRequest;
import com.goatlerbon.aim.client.service.impl.ClientInfo;
import com.goatlerbon.aim.client.thread.ContextHolder;
import com.goatlerbon.aim.client.vo.req.GoogleProtocolVo;
import com.goatlerbon.aim.client.vo.req.LoginReqVo;
import com.goatlerbon.aim.client.vo.res.AIMServerResVo;
import com.goatlerbon.aim.common.constant.Constants;
import com.goatlerbon.aim.common.protocol.AIMRequestProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.channels.SocketChannel;


@Component
public class AIMClient {

    @Autowired
    MsgHandle msgHandle;

    @Autowired
    EchoService echoService;

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    ClientInfo clientInfo;

    @Autowired
    RouteRequest routeRequest;

    @Autowired
    private ReConnectManager reConnectManager;

    private final static Logger LOGGER = LoggerFactory.getLogger(AIMClient.class);

    private EventLoopGroup group = new NioEventLoopGroup(0,new DefaultThreadFactory("aim-work"));

    private io.netty.channel.socket.SocketChannel socketChannel;

    /**
     * 重入次数
     */
    private int errorCount;
    /**
     * 用户ID
     */
    @Value("${aim.user.id}")
    private long userId;

    /**
     * 用户名
     */
    @Value("${aim.user.userName}")
    private String userName;

    //初始化方法在属性被注入之后调用
    @PostConstruct
    public void start(){
        //登入 + 获取可以使用的服务器 ip + port
        AIMServerResVo.ServerInfo aimServer = userLogin();

//        启动netty客户端
        startClient(aimServer);

//        向服务端登入
        loginAIMServer();
    }

    /**
     * 向路由服务器发起登入请求
     */
    private void loginAIMServer() {
        AIMRequestProto.AIMReqProtocol login = AIMRequestProto.AIMReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg(userName)
                .setType(Constants.CommandType.LOGIN)
                .build();
//        将登入的请求发送给服务器
        ChannelFuture future = socketChannel.writeAndFlush(login);
//        设置一个监听器
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                echoService.echo("Registry aim server success!");
            }
        });
    }

    /**
     * 获得服务器信息
     *
     * @return 路由服务器信息
     * @throws Exception
     */
    private AIMServerResVo.ServerInfo userLogin() {
        LoginReqVo loginReqVo = new LoginReqVo(userId,userName);
        AIMServerResVo.ServerInfo aimServer = null;
        try {
            aimServer = routeRequest.getAIMServer(loginReqVo);

            clientInfo.saveServiceInfo(aimServer.getIp() + ":" + aimServer.getAimServerPort())
                    .saveUserInfo(userId,userName);

            LOGGER.info("aimServer=[{}]", aimServer.toString());
        }catch (Exception e){
//            已经失败的次数
            errorCount++;

//            如果已经失败的次数大于 可失败次数 则停止客户端
            if(errorCount >= appConfiguration.getErrorCount()){
                echoService.echo("The maximum number of reconnections has been reached[{}]times, close cim client!", errorCount);
                msgHandle.shutdown();
            }
            LOGGER.error("login fail", e);
        }
        return aimServer;
    }

    /**
     * 启动客户端
     *
     * @param aimServer
     * @throws Exception
     */
    private void startClient(AIMServerResVo.ServerInfo aimServer) {
        Bootstrap bootstrap = new Bootstrap();
        //初始化设置netty
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new AIMClientHandleInitializer());
//        回调
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(aimServer.getIp(),aimServer.getAimServerPort()).sync();
        } catch (InterruptedException e) {
            errorCount++;
            if (errorCount >= appConfiguration.getErrorCount()) {
                LOGGER.error("连接失败次数达到上限[{}]次", errorCount);
                msgHandle.shutdown();
            }
            LOGGER.error("Connect fail!", e);
        }
        if (future.isSuccess()) {
            echoService.echo("Start aim client success!");
            LOGGER.info("启动 aim client 成功");
        }
        socketChannel = (io.netty.channel.socket.SocketChannel) future.channel();
    }

    /**
     * 关闭
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if (socketChannel != null){
            socketChannel.close();
        }
    }

    /**
     * 发送消息字符串
     *
     * @param msg
     */
    public void sendStringMsg(String msg){
        ByteBuf message = Unpooled.buffer(msg.getBytes().length);
        message.writeBytes(msg.getBytes());
        ChannelFuture future = socketChannel.writeAndFlush(message);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOGGER.info("客户端手动发消息成功={}", msg);
            }
        });
    }

    public void sendGoogleProtocolMsg(GoogleProtocolVo googleProtocolVo){
        AIMRequestProto.AIMReqProtocol protocol = AIMRequestProto.AIMReqProtocol.newBuilder()
                .setRequestId(googleProtocolVo.getRequestId())
                .setReqMsg(googleProtocolVo.getMsg())
                .setType(Constants.CommandType.MSG)
                .build();
        ChannelFuture future = socketChannel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发送 Google Protocol 成功={}", googleProtocolVo.toString()));
    }

    /**
     * 1.清除路由信息
     * 2.重新连接
     * 3.关闭重新连接任务
     * 4.重置重新连接状态。
     */
    public void reconnect() throws Exception{
//        如果说连接存活 则不需要 重新 连接
        if (socketChannel != null && socketChannel.isActive()) {
            return;
        }

        //首先清除路由信息，下线
        routeRequest.offLine();
        echoService.echo("aim server shutdown, reconnecting....");
        start();
        echoService.echo("Great! reConnect success!!!");

//        重新连接成功后 就关闭 定时 重新连接的任务
        reConnectManager.reConnectSuccess();
        ContextHolder.clear();
    }
}
