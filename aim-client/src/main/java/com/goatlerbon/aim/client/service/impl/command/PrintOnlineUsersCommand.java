package com.goatlerbon.aim.client.service.impl.command;

import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.InnerCommand;
import com.goatlerbon.aim.client.service.RouteRequest;
import com.goatlerbon.aim.client.vo.res.OnlineUsersResVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 查询所有在线用户指令 实现
 */
@Service
public class PrintOnlineUsersCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrintOnlineUsersCommand.class);


    @Autowired
    private RouteRequest routeRequest ;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        try {
            List<OnlineUsersResVo.DataBodyBean> onlineUsers = routeRequest.onlineUsers();

            echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (OnlineUsersResVo.DataBodyBean onlineUser : onlineUsers) {
                echoService.echo("userId={}=====userName={}",onlineUser.getUserId(),onlineUser.getUserName());
            }
            echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }
}
