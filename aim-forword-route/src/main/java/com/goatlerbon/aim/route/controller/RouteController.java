package com.goatlerbon.aim.route.controller;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.common.res.NULLBody;
import com.goatlerbon.aim.route.api.RouteApi;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.api.vo.req.LoginReqVo;
import com.goatlerbon.aim.route.api.vo.req.RegisterInfoReqVo;
import com.goatlerbon.aim.route.api.vo.res.AIMServerResVo;
import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;
import com.goatlerbon.aim.route.service.AccountService;
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

/**
 * 路由管理
 */
@Controller
public class RouteController implements RouteApi {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private UserInfoCacheService userInfoCacheService ;

    @Autowired
    private AccountService accountService;

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

//        String server =
        return response;
    }
}
