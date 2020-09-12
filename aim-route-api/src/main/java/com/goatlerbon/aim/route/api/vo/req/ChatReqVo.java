package com.goatlerbon.aim.route.api.vo.req;

import com.goatlerbon.aim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class ChatReqVo extends BaseRequest {
    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "用户ID", example = "1545574049323")
    private Long userId;

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "消息", example = "hello")
    private String msg;

    public ChatReqVo(){

    }

    public ChatReqVo(@NotNull(message = "userId 不能为空") Long userId, @NotNull(message = "msg 不能为空") String msg) {
        this.userId = userId;
        this.msg = msg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ChatReqVo{" +
                "userId=" + userId +
                ", msg='" + msg + '\'' +
                '}';
    }
}
