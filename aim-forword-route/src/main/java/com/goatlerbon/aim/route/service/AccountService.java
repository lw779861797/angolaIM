package com.goatlerbon.aim.route.service;

import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;

public interface AccountService {

    /**
     * 用户下线
     * @param userId 下线用户ID
     * @throws Exception
     */
    void offLine(Long userId) throws Exception;

    /**
     * 注册用户
     * @param info 用户信息
     * @return
     */
    RegisterInfoResVo register(RegisterInfoResVo info);
}
