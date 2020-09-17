package com.goatlerbon.aim.client.service.impl;

import com.goatlerbon.aim.client.config.AppConfiguration;
import com.goatlerbon.aim.client.service.InnerCommand;
import com.goatlerbon.aim.client.service.InnerCommandContext;
import com.goatlerbon.aim.client.service.MsgHandle;
import com.goatlerbon.aim.client.service.RouteRequest;
import com.goatlerbon.aim.client.vo.req.GroupReqVo;
import com.goatlerbon.aim.client.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgHandler implements MsgHandle {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgHandler.class);

    @Autowired
    private RouteRequest routeRequest;

    @Autowired
    private AppConfiguration appConfiguration;

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

    @Override
    public void shutdown() {

    }

    /**
     * 正常聊天
     * @param msg
     */
    public void normalChat(String msg) {
        String[] totalMsg = msg.split(";;");
//        私聊
        if(totalMsg.length > 1){
            SimpleChatReqVo simpleChatReqVo = new SimpleChatReqVo();
            simpleChatReqVo.setUserId(appConfiguration.getUserId());
            simpleChatReqVo.setReceiveUserId(Long.parseLong(totalMsg[0]));
            simpleChatReqVo.setMsg(totalMsg[1]);
            try {
                simpleChat(simpleChatReqVo);
            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        }else{
//            群聊
            GroupReqVo groupReqVO = new GroupReqVo(appConfiguration.getUserId(), msg);
            try {
                groupChat(groupReqVO);
            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        }
    }

    public void groupChat(GroupReqVo groupReqVO) throws Exception {
        routeRequest.sendGroupMsg(groupReqVO);
    }

    public void simpleChat(SimpleChatReqVo simpleChatReqVo) throws Exception {
        routeRequest.sendSimpleChatMsg(simpleChatReqVo);
    }

    /**
     * AI模式
     * @param msg
     */
    public void aiChat(String msg) {
        msg = msg.replace("吗", "");
        msg = msg.replace("嘛", "");
        msg = msg.replace("?", "!");
        msg = msg.replace("？", "!");
        msg = msg.replace("你", "我");
        System.out.println("AI:\033[31;4m" + msg + "\033[0m");
    }
}
