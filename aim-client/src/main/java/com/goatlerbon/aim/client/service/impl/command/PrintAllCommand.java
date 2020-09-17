package com.goatlerbon.aim.client.service.impl.command;

import com.goatlerbon.aim.client.enums.SystemCommandEnum;
import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.InnerCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class PrintAllCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrintAllCommand.class);

    @Autowired
    private EchoService echoService;

    @Override
    public void process(String msg) {
        Map<String ,String> allStatusCode = SystemCommandEnum.getAllStatusCode();
        echoService.echo("====================================");
        for(Map.Entry<String,String> stringStringEntry : allStatusCode.entrySet()){
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            echoService.echo(key + "---->" + value);
        }
        echoService.echo("====================================");
    }
}
