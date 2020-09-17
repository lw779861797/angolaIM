package com.goatlerbon.aim.client.service;

import com.goatlerbon.aim.client.vo.req.GroupReqVo;
import com.goatlerbon.aim.client.vo.req.LoginReqVo;
import com.goatlerbon.aim.client.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.client.vo.res.AIMServerResVo;

public interface RouteRequest {
    /**
     * 群发消息
     * @param groupReqVO 消息
     * @throws Exception
     */
    void sendGroupMsg(GroupReqVo groupReqVO) throws Exception;


    /**
     * 私聊
     * @param simpleChatReqVo
     * @throws Exception
     */
    void sendSimpleChatMsg(SimpleChatReqVo simpleChatReqVo)throws Exception;

    AIMServerResVo.ServerInfo getAIMServer(LoginReqVo loginReqVo);
}
