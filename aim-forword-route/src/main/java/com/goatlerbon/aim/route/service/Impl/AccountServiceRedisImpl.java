package com.goatlerbon.aim.route.service.Impl;

import com.goatlerbon.aim.route.service.AccountService;
import com.goatlerbon.aim.route.service.UserInfoCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.goatlerbon.aim.route.constant.Constant.ROUTE_PREFIX;

@Service
public class AccountServiceRedisImpl implements AccountService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoCacheService userInfoCacheService;

    @Override
    public void offLine(Long userId) throws Exception {
        // TODO: 2019-01-21 改为一个原子命令，以防数据不一致性 这块没明白

        //删除路由
        redisTemplate.delete(ROUTE_PREFIX + userId);

        //删除登录状态
        userInfoCacheService.removeLoginStatus(userId);
    }
}
