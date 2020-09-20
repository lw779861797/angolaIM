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

@Service
public class PrefixSearchCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrefixSearchCommand.class);

    @Autowired
    private RouteRequest routeRequest;

    private EchoService echoService;
    @Override
    public void process(String msg) {
        try {
            List<OnlineUsersResVo.DataBodyBean> onlineUsers = routeRequest.onlineUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
