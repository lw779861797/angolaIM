package com.goatlerbon.aim.route;

import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.common.res.NULLBody;
import com.goatlerbon.aim.route.api.RouteApi;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.service.AccountService;
import com.goatlerbon.aim.route.service.UserInfoCacheService;
import io.swagger.annotations.ApiOperation;
import okhttp3.Response;
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

    @Autowired
    private UserInfoCacheService userInfoCacheService ;

    @Autowired
    private AccountService accountService;

    @ApiOperation("客户端下线")
    @RequestMapping(value = "offLine", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public BaseResponse<NULLBody> offLine(@RequestBody ChatReqVo groupReqVO) {
        BaseResponse<NULLBody> response = new BaseResponse();

        AIMUserInfo userInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());
        return null;
    }
}
