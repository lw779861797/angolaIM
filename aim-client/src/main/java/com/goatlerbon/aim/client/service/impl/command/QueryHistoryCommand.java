package com.goatlerbon.aim.client.service.impl.command;

import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.InnerCommand;
import com.goatlerbon.aim.client.service.MsgLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 查询历史记录指令 实现
 */
@Service
public class QueryHistoryCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(QueryHistoryCommand.class);


    @Autowired
    private MsgLogger msgLogger ;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        String[] split = msg.split(" ");
        if (split.length < 2){
            return;
        }
        String res = msgLogger.query(split[1]);
        echoService.echo(res);
    }
}
