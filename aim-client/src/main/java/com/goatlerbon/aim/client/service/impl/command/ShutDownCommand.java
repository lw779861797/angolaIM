package com.goatlerbon.aim.client.service.impl.command;

import com.goatlerbon.aim.client.client.AIMClient;
import com.goatlerbon.aim.client.service.*;
import com.goatlerbon.aim.common.data.construct.RingBufferWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ShutDownCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShutDownCommand.class);

    @Autowired
    private RouteRequest routeRequest;

    @Autowired
    private AIMClient aimClient;

    @Autowired
    private MsgLogger msgLogger;

    @Resource(name = "callBackThreadPool")
    private ThreadPoolExecutor callBackExecutor;

    @Autowired
    private EchoService echoService;

    @Autowired
    private ShutDownMsg shutDownMsg;

    //    环形队列，用来延迟任务。
    @Autowired
    private RingBufferWheel ringBufferWheel;

    @Override
    public void process(String msg) {
        echoService.echo("aim client closing...");
        //设置中断标志
        shutDownMsg.shutdown();
        //用户下线
        routeRequest.offLine();
        //信息日志线程关系
        msgLogger.stop();
        callBackExecutor.shutdown();

//        关闭环形队列
        ringBufferWheel.stop(false);
        try {
            /**
             * 当前线程阻塞，直到
             *      等所有已提交的任务（包括正在跑的和队列中等待的）执行完
             *      或者等超时时间到
             *      或者线程被中断，抛出InterruptedException
             */
            while (!callBackExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                echoService.echo("thread pool closing");
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
