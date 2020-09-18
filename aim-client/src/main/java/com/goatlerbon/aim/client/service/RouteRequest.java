package com.goatlerbon.aim.client.service;

import com.goatlerbon.aim.client.vo.req.GroupReqVo;
import com.goatlerbon.aim.client.vo.req.LoginReqVo;
import com.goatlerbon.aim.client.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.client.vo.res.AIMServerResVo;
import com.goatlerbon.aim.client.vo.res.OnlineUsersResVo;

import java.util.List;

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

    /**
     * 获取服务器
     * @return 服务ip+port
     * @param loginReqVo
     * @throws Exception
     */
    AIMServerResVo.ServerInfo getAIMServer(LoginReqVo loginReqVo);

    /**
     * 用户下线
     */
    void offLine();

    /**
     * 获取所有在线用户
     * @return
     * @throws Exception
     */
    List<OnlineUsersResVo.DataBodyBean> onlineUsers()throws Exception ;
}
