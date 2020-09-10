package com.goatlerbon.aim.server.api.vo.req;


import com.goatlerbon.aim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * 消息发送实体
 */
public class SendMsgReqVo extends BaseRequest {
    @NotNull(message = "消息不能为空")
    @ApiModelProperty(required = true,value = "消息")
    private String msg;

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(required = true,value = "用户ID")
    private Long userId;

    public SendMsgReqVo(String msg, Long userId) {
        this.msg = msg;
        this.userId = userId;
    }

    public SendMsgReqVo() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SendMsgReqVo{" +
                "msg='" + msg + '\'' +
                ", userId=" + userId +
                '}';
    }
}
