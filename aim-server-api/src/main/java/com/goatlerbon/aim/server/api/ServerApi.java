package com.goatlerbon.aim.server.api;

import com.goatlerbon.aim.server.api.vo.req.SendMsgReqVo;

public interface ServerApi {

    /**
     * 发送消息到客户端
     * @param sendMsgReqVo
     * @return
     * @throws Exception
     */
    Object sendMsg(SendMsgReqVo sendMsgReqVo) throws Exception;
}
