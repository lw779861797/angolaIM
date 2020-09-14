package com.goatlerbon.aim.client.scan;

import com.goatlerbon.aim.client.AppConfiguration;
import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.MsgHandle;
import com.goatlerbon.aim.client.service.MsgLogger;
import com.goatlerbon.aim.client.util.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    }
}
