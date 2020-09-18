package com.goatlerbon.aim.route.controller;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.pojo.RouteInfo;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.common.res.NULLBody;
import com.goatlerbon.aim.common.route.algorithm.RouteHandle;
import com.goatlerbon.aim.common.util.RouteInfoParseUtil;
import com.goatlerbon.aim.route.api.RouteApi;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.api.vo.req.LoginReqVo;
import com.goatlerbon.aim.route.api.vo.req.RegisterInfoReqVo;
import com.goatlerbon.aim.route.api.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.route.api.vo.res.AIMServerResVo;
import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;
import com.goatlerbon.aim.route.cache.ServerCache;
import com.goatlerbon.aim.route.service.AccountService;
import com.goatlerbon.aim.route.service.CommonBizService;
import com.goatlerbon.aim.route.service.UserInfoCacheService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Set;

/**
 * 路由管理
 */
@Controller
public class RouteController implements RouteApi {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private CommonBizService commonBizService ;

    @Autowired
    private UserInfoCacheService userInfoCacheService ;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RouteHandle routeHandle;

    @Autowired
    private ServerCache serverCache;

    @ApiOperation("客户端下线")
    @RequestMapping(value = "offLine", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public BaseResponse<NULLBody> offLine(@RequestBody ChatReqVo groupReqVO) throws Exception {
        BaseResponse<NULLBody> response = new BaseResponse();

        /**
         * 从缓存中获取用户信息
         */
        AIMUserInfo userInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());

        LOGGER.info("user [{}] offline!", userInfo.toString());

//        下线用户
        accountService.offLine(groupReqVO.getUserId());

        response.setCode(StatusEnum.SUCCESS.getCode());
        response.setMessage(StatusEnum.SUCCESS.getMessage());
        return response;
    }

    /**
     * 注册账号
     */
    @ApiOperation("注册账号")
    @RequestMapping(value = "registerAccount", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public BaseResponse<RegisterInfoResVo> registerAccount(@RequestBody RegisterInfoReqVo registerInfoReqVo) throws Exception{
        BaseResponse<RegisterInfoResVo> response = new BaseResponse<>();

        // TODO: 这边用户ID可以优化
        long userId = System.currentTimeMillis();
        RegisterInfoResVo info = new RegisterInfoResVo(userId,registerInfoReqVo.getUserName());

        info = accountService.register(info);

        response.setDataBody(info);
        response.setCode(StatusEnum.SUCCESS.getCode());
        response.setMessage(StatusEnum.SUCCESS.getMessage());
        return response;
    }

    /**
     * 获取一台 AIM server 如果服务器有多台则使用负载均衡算法获取一台服务器
     * AIMServerResVo 服务器信息 IP 端口号等等
     * @return
     */
    @ApiOperation("登录并获取服务器")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public BaseResponse<AIMServerResVo> login(@RequestBody LoginReqVo loginReqVo) throws Exception{
        BaseResponse<AIMServerResVo> response = new BaseResponse<>();

        // 使用loginReqVo.getUserId() 经过一致性hash算法
        // 这个用户使用的服务器 是 用户ID hash 完 最近的下一个结点的服务器，每次都会访问相同的服务器
        String server = routeHandle.routeServer(serverCache.getServerList(),String.valueOf(loginReqVo.getUserId()));
        LOGGER.info("userName=[{}] route server info=[{}]", loginReqVo.getUserName(), server);

        //将 server中的信息 解析
        RouteInfo routeInfo = RouteInfoParseUtil.parse(server);

        /**
         * 尝试连接 检查服务器的状态
         */
        commonBizService.checkServerAvailable(routeInfo);

        //TODO： 以后给完善成 数据库操作
        //登入效验
        StatusEnum statusEnum = accountService.login(loginReqVo);

        if(statusEnum == StatusEnum.SUCCESS){

            //保存路由信息
            accountService.saveRouteInfo(loginReqVo,server);

            AIMServerResVo vo = new AIMServerResVo(routeInfo);
            response.setDataBody(vo);
        }
        response.setCode(statusEnum.getCode());
        response.setMessage(statusEnum.getMessage());

        return response;
    }

    @ApiOperation("私聊 API")
    @RequestMapping(value = "simpleRoute", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public BaseResponse<NULLBody> simpleRoute(@RequestBody SimpleChatReqVo chatReqVo)throws Exception{
        BaseResponse<NULLBody> response = new BaseResponse<>();
        try {
            //获取接收消息用户的路由信息
            AIMServerResVo serverResVo = accountService.loadRouteRelatedByUserId(chatReqVo.getReceiveUserId());

//            ReceiveUserId 是接收者的ID
            ChatReqVo chatVo = new ChatReqVo(chatReqVo.getReceiveUserId(),chatReqVo.getMsg());

//            向服务器发送消息
            accountService.pushMsg(serverResVo,chatReqVo.getUserId(),chatVo);

            response.setCode(StatusEnum.SUCCESS.getCode());
            response.setMessage(StatusEnum.SUCCESS.getMessage());
        }catch (AIMException e){
            response.setCode(e.getErrorCode());
            response.setMessage(e.getErrorMessage());
        }
        return response;
    }

    @ApiOperation("群聊 API")
    @RequestMapping(value = "groupRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public Object groupRoute(@RequestBody ChatReqVo groupReqVo) throws Exception{
        BaseResponse<NULLBody> response = new BaseResponse();
        LOGGER.info("msg=[{}]", groupReqVo.toString());

        //获取所有的推送列表
        Map<Long,AIMServerResVo> serverResVoMap = accountService.loadRouteRelated();
        for(Map.Entry<Long,AIMServerResVo> aimServerResVoEntry :serverResVoMap.entrySet()){
            Long userId = aimServerResVoEntry.getKey();
            AIMServerResVo cimServerResVO = aimServerResVoEntry.getValue();

            //过滤掉自己
            if (userId.equals(groupReqVo.getUserId())){
                AIMUserInfo cimUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVo.getUserId());
                LOGGER.warn("过滤掉了发送者 userId={}",cimUserInfo.toString());
                continue;
            }

            //推送消息
            ChatReqVo chatVO = new ChatReqVo(userId,groupReqVo.getMsg()) ;
            accountService.pushMsg(cimServerResVO ,groupReqVo.getUserId(),chatVO);
        }
        response.setCode(StatusEnum.SUCCESS.getCode());
        response.setMessage(StatusEnum.SUCCESS.getMessage());
        return response;
    }

    /**
     * 获取所有在线用户
     *
     * @return
     */
    @ApiOperation("获取所有在线用户")
    @RequestMapping(value = "onlineUser", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public BaseResponse<Set<AIMUserInfo>> onlineUser() {
        BaseResponse<Set<AIMUserInfo>> response = new BaseResponse<>();

        Set<AIMUserInfo> aimUserInfos = userInfoCacheService.onlineUser();
        response.setCode(StatusEnum.SUCCESS.getCode());
        response.setMessage(StatusEnum.SUCCESS.getMessage());
        response.setDataBody(aimUserInfos);
        return response;
    }
}
