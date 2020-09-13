package com.goatlerbon.aim.route.api.vo.res;

import java.io.Serializable;

public class RegisterInfoResVo implements Serializable {
    private Long userId;

    private String userName;

    public RegisterInfoResVo(Long userId, String userName) {
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
        return "RegisterInfoResVo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
