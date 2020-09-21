package com.goatlerbon.aim.client.service.impl;

import com.goatlerbon.aim.client.service.CustomMsgHandleListener;
import com.goatlerbon.aim.client.service.MsgLogger;
import com.goatlerbon.aim.client.util.SpringBeanFactory;

/**
 * 自定义收到消息后 回调监听 并存入日志
 */
public class MsgCallBackListener implements CustomMsgHandleListener{

    private MsgLogger msgLogger;

    public MsgCallBackListener(){
        this.msgLogger = SpringBeanFactory.getBean(MsgLogger.class);
    }

    @Override
    public void handle(String msg) {
        msgLogger.log(msg);
    }
}
