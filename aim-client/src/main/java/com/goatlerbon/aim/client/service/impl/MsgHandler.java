package com.goatlerbon.aim.client.service.impl;

import com.goatlerbon.aim.client.service.InnerCommand;
import com.goatlerbon.aim.client.service.InnerCommandContext;
import com.goatlerbon.aim.client.service.MsgHandle;
import com.goatlerbon.aim.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MsgHandler implements MsgHandle {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgHandler.class);

    private InnerCommandContext commandContext;

    private boolean aiModel = false;

    @Override
    public boolean checkMsg(String msg) {
        if(StringUtil.isEmpty(msg)){
            LOGGER.warn("不能发送空消息！");
            return true;
        }
        return false;
    }

    @Override
    public boolean innerCommand(String msg) {
        if(msg.startsWith(":")){
            InnerCommand instance = commandContext.getInstance(msg);
            instance.process(msg);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void sendMsg(String msg) {
        //是否使用 人工算法 类似机器人回答
        if (aiModel) {
            aiChat(msg);
        } else {
            normalChat(msg);
        }
    }

    public void normalChat(String msg) {
    }

    public void aiChat(String msg) {
    }
}
