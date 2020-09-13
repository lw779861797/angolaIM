package com.goatlerbon.aim.route.service;

import com.goatlerbon.aim.common.pojo.AIMUserInfo;

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
     * 保存登录状态
     * @param userId
     * @return
     */
    boolean saveAndCheckUserLoginStatus(Long userId);
}
