package com.goatlerbon.aim.route.api;

import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.api.vo.req.LoginReqVo;
import com.goatlerbon.aim.route.api.vo.req.RegisterInfoReqVo;
import com.goatlerbon.aim.route.api.vo.res.AIMServerResVo;
import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;
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

    BaseResponse<AIMServerResVo> login(@RequestBody LoginReqVo loginReqVo) throws Exception;
}
