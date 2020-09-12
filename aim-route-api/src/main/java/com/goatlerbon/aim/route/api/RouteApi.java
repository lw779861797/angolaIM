package com.goatlerbon.aim.route.api;

import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import okhttp3.Response;

public interface RouteApi {
    /**
     * 用户下线
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object offLine(ChatReqVo groupReqVO)throws Exception;
}
