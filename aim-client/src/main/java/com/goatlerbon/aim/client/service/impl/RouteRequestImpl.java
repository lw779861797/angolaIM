package com.goatlerbon.aim.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.RouteRequest;
import com.goatlerbon.aim.client.thread.ContextHolder;
import com.goatlerbon.aim.client.vo.req.GroupReqVo;
import com.goatlerbon.aim.client.vo.req.LoginReqVo;
import com.goatlerbon.aim.client.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.client.vo.res.AIMServerResVo;
import com.goatlerbon.aim.common.core.proxy.ProxyManager;
import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.route.api.RouteApi;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RouteRequestImpl implements RouteRequest{
    private final static Logger LOGGER = LoggerFactory.getLogger(RouteRequestImpl.class);

    @Autowired
    EchoService echoService;

    @Autowired
    OkHttpClient okHttpClient;

    @Value("${aim.route.url}")
    private String routeUrl ;

    @Override
    public void sendGroupMsg(GroupReqVo groupReqVO) throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();
        ChatReqVo chatReqVO = new ChatReqVo(groupReqVO.getUserId(), groupReqVO.getMsg()) ;
        Response response = null;
        try {
            response = (Response)routeApi.groupRoute(chatReqVO);
        }catch (Exception e){
            LOGGER.error("exception",e);
        }finally {
            response.body().close();
        }
    }

    /**
     * 发送私聊消息
     * @param simpleChatReqVo
     * @throws Exception
     */
    @Override
    public void sendSimpleChatMsg(SimpleChatReqVo simpleChatReqVo) throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class,routeUrl,okHttpClient).getInstance();
        com.goatlerbon.aim.route.api.vo.req.SimpleChatReqVo vo = new com.goatlerbon.aim.route.api.vo.req.SimpleChatReqVo();
        vo.setMsg(simpleChatReqVo.getMsg());
        vo.setReceiveUserId(simpleChatReqVo.getReceiveUserId());
        vo.setUserId(simpleChatReqVo.getUserId());

        Response response = null;
        try {
            //发出私聊信息
            response = (Response) routeApi.simpleRoute(vo);
            String json = response.body().string();
            BaseResponse baseResponse = JSON.parseObject(json,BaseResponse.class);

            // 对方用户不在线
            if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())){
                LOGGER.error(simpleChatReqVo.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
            }
        }catch (Exception e){
            LOGGER.error("exception",e);
        }finally {
            response.body().close();
        }
    }

    /**
     * 获取服务器信息
     * @param loginReqVo
     * @return
     */
    @Override
    public AIMServerResVo.ServerInfo getAIMServer(LoginReqVo loginReqVo) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class,routeUrl,okHttpClient).getInstance();
        com.goatlerbon.aim.route.api.vo.req.LoginReqVo vo = new com.goatlerbon.aim.route.api.vo.req.LoginReqVo();
        vo.setUserId(loginReqVo.getUserId());
        vo.setUserName(loginReqVo.getUserName());

        Response response = null;
        AIMServerResVo aimServerResVo = null;

        try {
            response = (Response) routeApi.login(vo);
            String json = response.body().string();
            aimServerResVo = JSON.parseObject(json, AIMServerResVo.class);

            if(!aimServerResVo.getCode().equals(StatusEnum.SUCCESS.getCode())){
                echoService.echo(aimServerResVo.getMessage());

                //TODO : 没明白什么意思
//                当客户端处于重新连接状态时，这不能存在。
                // when client in reConnect state, could not exit.
                if (ContextHolder.getReconnect()){
                    echoService.echo("###{}###", StatusEnum.RECONNECT_FAIL.getMessage());
                    throw new AIMException(StatusEnum.RECONNECT_FAIL);
                }

                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            response.body().close();
        }
        return aimServerResVo.getDataBody();
    }

    @Override
    public void offLine() {

    }
}
