package com.goatlerbon.aim.client.vo.req;

import com.goatlerbon.aim.common.req.BaseRequest;

public class LoginReqVo extends BaseRequest {
    private Long userId;

    private String userName;

    public LoginReqVo(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "LoginReqVo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
