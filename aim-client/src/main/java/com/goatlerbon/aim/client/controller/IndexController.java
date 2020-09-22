package com.goatlerbon.aim.client.controller;

import com.goatlerbon.aim.client.client.AIMClient;
import com.goatlerbon.aim.client.service.RouteRequest;
import com.goatlerbon.aim.client.vo.req.GoogleProtocolVo;
import com.goatlerbon.aim.client.vo.req.GroupReqVo;
import com.goatlerbon.aim.client.vo.req.SendMsgReqVo;
import com.goatlerbon.aim.client.vo.req.StringReqVo;
import com.goatlerbon.aim.client.vo.res.SendMsgResVo;
import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.res.BaseResponse;
import com.goatlerbon.aim.common.res.NULLBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
    @Autowired
    private AIMClient heartbeatClient ;

    @Autowired
    private RouteRequest routeRequest ;

    /**
     * 向服务端发消息 字符串 单纯向服务器发消息
     * @param stringReqVo
     * @return
     */
    @ApiOperation("客户端发送消息，字符串")
    @RequestMapping(value = "/sendStringMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NULLBody> sendStringMsg(@RequestBody StringReqVo stringReqVo){
        BaseResponse<NULLBody> res = new BaseResponse();

        for (int i = 0; i < 100; i++) {
            heartbeatClient.sendStringMsg(stringReqVo.getMsg()) ;
        }

        SendMsgResVo sendMsgResVO = new SendMsgResVo() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }

    /**
     * 向服务端发消息 Google ProtoBuf
     * @param googleProtocolVo
     * @return
     */
    @ApiOperation("向服务端发消息 Google ProtoBuf")
    @RequestMapping(value = "/sendProtoBufMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NULLBody> sendProtoBufMsg(@RequestBody GoogleProtocolVo googleProtocolVo){
        BaseResponse<NULLBody> res = new BaseResponse();

        //TODO:测试
        for (int i = 0; i < 100; i++) {
            heartbeatClient.sendGoogleProtocolMsg(googleProtocolVo) ;
        }

        SendMsgResVo sendMsgResVO = new SendMsgResVo() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }

    /**
     * 群发消息
     * @param sendMsgReqVo
     * @return
     */
    @ApiOperation("群发消息")
    @RequestMapping(value = "/sendGroupMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendGroupMsg(@RequestBody SendMsgReqVo sendMsgReqVo) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        GroupReqVo groupReqVO = new GroupReqVo(sendMsgReqVo.getUserId(),sendMsgReqVo.getMsg()) ;
        routeRequest.sendGroupMsg(groupReqVO) ;

        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }
}
