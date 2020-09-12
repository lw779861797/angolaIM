package com.goatlerbon.aim.route.service;

import com.goatlerbon.aim.common.pojo.AIMUserInfo;

public interface UserInfoCacheService {
    AIMUserInfo loadUserInfoByUserId(Long userId);
}
