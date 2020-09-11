package com.goatlerbon.aim.server.server;

import com.goatlerbon.aim.server.init.AIMServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * https://www.cnblogs.com/georgexu/p/10909814.html
 * 长连接可以省去较多的TCP建立和关闭的操作，减少浪费，节约时间。
 *
 * 对于频繁请求资源的客户来说，较适用长连接。
 *
 * client与server之间的连接如果一直不关闭的话，会存在一个问题，
 *
 * 随着客户端连接越来越多，server早晚有扛不住的时候，这时候server端需要采取一些策略，
 *
 * 如关闭一些长时间没有读写事件发生的连接，这样可以避免一些恶意连接导致server端服务受损；
 *
 * 如果条件再允许就可以以客户端机器为颗粒度，限制每个客户端的最大长连接数，
 *
 * 这样可以完全避免某个蛋疼的客户端连累后端服务。
 *
 * 短连接对于服务器来说管理较为简单，存在的连接都是有用的连接，不需要额外的控制手段。
 * 但如果客户请求频繁，将在TCP的建立和关闭操作上浪费时间和带宽。
 */
/**
 * 长连接多用于操作频繁，点对点的通讯，而且连接数不能太多情况。
 *
 * 每个TCP连接都需要三次握手，这需要时间，如果每个操作都是先连接，
 *
 * 再操作的话那么处理速度会降低很多，所以每个操作完后都不断开，
 *
 * 再次处理时直接发送数据包就OK了，不用建立TCP连接。
 *
 * 例如：数据库的连接用长连接，如果用短连接频繁的通信会造成socket错误，
 *
 * 而且频繁的socket 创建也是对资源的浪费。
 *
 * 而像WEB网站的http服务一般都用短链接，因为长连接对于服务端来说会耗费一定的资源，
 *
 * 而像WEB网站这么频繁的成千上万甚至上亿客户端的连接用短连接会更省一些资源，
 *
 * 如果用长连接，而且同时有成千上万的用户，如果每个用户都占用一个连接的话，
 *
 * 那可想而知吧。所以并发量大，但每个用户无需频繁操作情况下需用短连好。
 *
 */
/**
 * netty服务类
 */
@Component
public class AIMServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AIMServer.class);

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    @Value("${aim.server.port")
    private int nettyPort;

    /**
     * 启动 netty server
     */
    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(nettyPort))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new AIMServerInitializer());
        ChannelFuture future = bootstrap.bind().sync();
        if(future.isSuccess()){
            LOGGER.info("Start aim server success!!!");
        }
    }

    /**
     * 销毁 关闭netty服务
     */
    public void destory(){
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        LOGGER.info("Close aim server success!!!");
    }
}
