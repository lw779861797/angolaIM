package com.goatlerbon.aim.server.kit;

import com.goatlerbon.aim.common.kit.HeartBeatHandler;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.util.NettyAttrUtil;
import com.goatlerbon.aim.server.config.AppConfiguration;
import com.goatlerbon.aim.server.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 心跳服务类
 */
public class ServerHeartBeatHandlerImpl implements HeartBeatHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHeartBeatHandlerImpl.class);

    @Autowired
    private RouteHandler routeHandler ;

    @Autowired
    private AppConfiguration appConfiguration ;

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        long heartBeatTime = appConfiguration.getHeartBeatTime() * 1000;

        Long lastReadTime = NettyAttrUtil.getReaderTime(ctx.channel());

        long now = System.currentTimeMillis();
        if (lastReadTime != null && now - lastReadTime > heartBeatTime) {
            AIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
            if(userInfo != null){
                LOGGER.warn("客户端[{}]心跳超时[{}]ms，需要关闭连接!",userInfo.getUserName(),now - lastReadTime);
            }
            /**
             * 向 http://localhost:8003/userOffLine 这个路由发送http请求 做一个请求用户下线的操作
             * 主要是通过 动态代理技术 在调用接口的时候 由 代理对象帮你发请求 到路由服务器
             */
            routeHandler.userOffLine(userInfo, (NioSocketChannel) ctx.channel());
            ctx.channel().close();
        }
    }
}
