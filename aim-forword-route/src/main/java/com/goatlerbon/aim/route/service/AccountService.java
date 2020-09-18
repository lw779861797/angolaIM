package com.goatlerbon.aim.route.service;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.api.vo.req.LoginReqVo;
import com.goatlerbon.aim.route.api.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.route.api.vo.res.AIMServerResVo;
import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;

import java.util.Map;

public interface AccountService {

    /**
     * 用户下线
     * @param userId 下线用户ID
     * @throws Exception
     */
    void offLine(Long userId) throws Exception;

    /**
     * 注册用户
     * @param info 用户信息
     * @return
     */
    RegisterInfoResVo register(RegisterInfoResVo info);

    /**
     * 登入
     * @param loginReqVo
     * @return
     */
    StatusEnum login(LoginReqVo loginReqVo);

    /**
     * 保存路由信息
     * @param loginReqVo
     * @param server
     */
    void saveRouteInfo(LoginReqVo loginReqVo, String server);

    /**
     * 获取接收消息用户的路由信息
     * @param receiveUserId
     * @return
     */
    AIMServerResVo loadRouteRelatedByUserId(Long receiveUserId);

    /**
     * 向服务器发送消息
     * @param serverResVo
     * @param userId
     * @param chatReqVo
     */
    void pushMsg(AIMServerResVo serverResVo, Long userId, ChatReqVo chatReqVo);

    /**
     * 加载所有用户的路有关系
     * @return 所有的路由关系
     */
    Map<Long, AIMServerResVo> loadRouteRelated();
}
