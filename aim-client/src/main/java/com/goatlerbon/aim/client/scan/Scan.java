package com.goatlerbon.aim.client.scan;

import com.goatlerbon.aim.client.config.AppConfiguration;
import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.MsgHandle;
import com.goatlerbon.aim.client.service.MsgLogger;
import com.goatlerbon.aim.client.util.SpringBeanFactory;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Scan implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);

    /**
     * 系统参数
     */
    private AppConfiguration configuration;

    private MsgHandle msgHandle;

    private MsgLogger msgLogger;

    private EchoService echoService;

    public Scan() {
        this.configuration = SpringBeanFactory.getBean(AppConfiguration.class);
        this.msgHandle = SpringBeanFactory.getBean(MsgHandle.class) ;
        this.msgLogger = SpringBeanFactory.getBean(MsgLogger.class) ;
        this.echoService = SpringBeanFactory.getBean(EchoService.class) ;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while(true){
            String msg = sc.nextLine();

            //消息检查
            if(msgHandle.checkMsg(msg)){
                continue;
            }

            //系统内置命令
            if (msgHandle.innerCommand(msg)){
                continue;
            }

            //真正的发送消息
            msgHandle.sendMsg(msg) ;

            //写入聊天记录
            msgLogger.log(msg) ;

            echoService.echo(EmojiParser.parseToUnicode(msg));
        }
    }
}
