package com.goatlerbon.aim.route.api;

import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.common.res.NULLBody;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.api.vo.req.LoginReqVo;
import com.goatlerbon.aim.route.api.vo.req.RegisterInfoReqVo;
import com.goatlerbon.aim.route.api.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.route.api.vo.res.AIMServerResVo;
import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;
import okhttp3.Response;
import org.springframework.web.bind.annotation.RequestBody;

public interface RouteApi {
    /**
     * 用户下线
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object offLine(ChatReqVo groupReqVO)throws Exception;

    /**
     * 注册账号
     * @param registerInfoReqVo
     * @return
     * @throws Exception
     */
    BaseResponse<RegisterInfoResVo> registerAccount(@RequestBody RegisterInfoReqVo registerInfoReqVo) throws Exception;

    /**
     * 获取一台 AIM server 如果服务器有多台则使用负载均衡算法获取一台服务器
     * AIMServerResVo 服务器信息 IP 端口号等等
     * @return
     */
    Object login(@RequestBody LoginReqVo loginReqVo) throws Exception;

    /**
     * 私聊
     * @param chatReqVo
     * @return
     * @throws Exception
     */
    Object simpleRoute(@RequestBody SimpleChatReqVo chatReqVo)throws Exception;

    /**
     * 群聊
     * @param groupReqVo
     * @return
     */
    Object groupRoute(ChatReqVo groupReqVo) throws Exception;

    /**
     * 获取所有在线用户
     * @return
     */
    Object onlineUser();
}
