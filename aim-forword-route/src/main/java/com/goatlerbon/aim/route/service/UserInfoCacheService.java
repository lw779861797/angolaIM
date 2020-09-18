package com.goatlerbon.aim.route.service;

import com.goatlerbon.aim.common.pojo.AIMUserInfo;

import java.util.Set;

public interface UserInfoCacheService {
    /**
     * 这边没有使用 数据库 存取数据信息 后续版本逐渐修改 用了双重缓存 存取用户信息
     * @param userId
     * @return
     */
    AIMUserInfo loadUserInfoByUserId(Long userId);

    /**
     * 删除登录状态
     * @param userId
     */
    void removeLoginStatus(Long userId);

    /**
     * 保存和检查用户登录情况
     * @param userId userId 用户唯一 ID
     * @return true 为可以登录 false 为已经登录
     * @throws Exception
     */
    boolean saveAndCheckUserLoginStatus(Long userId);

    /**
     * 查询所有在线的用户
     * @return
     */
    Set<AIMUserInfo> onlineUser();
}
