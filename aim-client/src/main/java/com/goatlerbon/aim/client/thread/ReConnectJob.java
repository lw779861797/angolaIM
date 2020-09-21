package com.goatlerbon.aim.client.thread;

import com.goatlerbon.aim.client.service.impl.ClientHeartBeatHandlerImpl;
import com.goatlerbon.aim.client.util.SpringBeanFactory;
import com.goatlerbon.aim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重新连接任务
 */
public class ReConnectJob implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReConnectJob.class);

    private ChannelHandlerContext context;

    private HeartBeatHandler heartBeatHandler;

    public ReConnectJob(ChannelHandlerContext context){
        this.context = context;
        this.heartBeatHandler = SpringBeanFactory.getBean(ClientHeartBeatHandlerImpl.class);
    }
    @Override
    public void run() {
        try {
            //执行重新连接操作
            heartBeatHandler.process(context);
        } catch (Exception e) {
            LOGGER.error("Exception",e);
        }
    }
}
