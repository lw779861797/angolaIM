package com.goatlerbon.aim.route;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.common.res.NULLBody;
import com.goatlerbon.aim.route.api.RouteApi;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.service.AccountService;
import com.goatlerbon.aim.route.service.UserInfoCacheService;
import io.swagger.annotations.ApiOperation;
import okhttp3.Response;
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
}
