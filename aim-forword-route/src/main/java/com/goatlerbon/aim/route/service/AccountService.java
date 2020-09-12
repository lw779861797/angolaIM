package com.goatlerbon.aim.route.service;

public interface AccountService {

    /**
     * 用户下线
     * @param userId 下线用户ID
     * @throws Exception
     */
    void offLine(Long userId) throws Exception;
}
