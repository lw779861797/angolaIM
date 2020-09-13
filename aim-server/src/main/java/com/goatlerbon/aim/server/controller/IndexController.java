package com.goatlerbon.aim.server.controller;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.server.api.vo.req.SendMsgReqVo;
import com.goatlerbon.aim.server.api.vo.res.SendMsgResVo;
import com.goatlerbon.aim.server.server.AIMServer;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 服务器controller
 */
@Controller
public class IndexController {

    @Autowired
    private AIMServer aimServer;

    @ApiOperation("发送消息到客户端")
    @RequestMapping(value = "/sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVo> sendMsg(@RequestBody SendMsgReqVo sendMsgReqVo){
        BaseResponse<SendMsgResVo> response = new BaseResponse<>();
        aimServer.sendMsg(sendMsgReqVo);
        SendMsgResVo sendMsgResVo = new SendMsgResVo();
        sendMsgResVo.setMsg("OK");
        response.setCode(StatusEnum.SUCCESS.getCode());
        response.setMessage(StatusEnum.SUCCESS.getMessage());
        response.setDataBody(sendMsgResVo);
        return response;
    }
}
