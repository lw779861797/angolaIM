package com.goatlerbon.aim.route.service.Impl;

import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.route.service.UserInfoCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.goatlerbon.aim.route.constant.Constant.ACCOUNT_PREFIX;
import static com.goatlerbon.aim.route.constant.Constant.LOGIN_STATUS_PREFIX;

@Service
public class UserInfoCacheServiceImpl implements UserInfoCacheService {

    /**
     * todo 本地缓存，为了防止内存撑爆，后期可换为 LRU。
     */
    private final static Map<Long,AIMUserInfo> USER_INFO_MAP = new ConcurrentHashMap<>(64);

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 这边没有使用 数据库 存取数据信息 后续版本逐渐修改 用了双重缓存 存取用户信息
     * @param userId
     * @return
     */
    @Override
    public AIMUserInfo loadUserInfoByUserId(Long userId) {
        AIMUserInfo userInfo = USER_INFO_MAP.get(userId);

        //优先从本地缓存获取
        if(userInfo != null){
            return userInfo;
        }

//        从redis中获取
        String sendUserName = redisTemplate.opsForValue().get(ACCOUNT_PREFIX + userId);
        if(sendUserName != null){
            userInfo = new AIMUserInfo(userId,sendUserName);
            USER_INFO_MAP.put(userId,userInfo);
        }
        return userInfo;
    }

    @Override
    public void removeLoginStatus(Long userId) {
        redisTemplate.opsForSet().remove(LOGIN_STATUS_PREFIX,userId.toString()) ;
    }

    @Override
    public boolean saveAndCheckUserLoginStatus(Long userId) {
        Long add = redisTemplate.opsForSet().add(LOGIN_STATUS_PREFIX, userId.toString());
        if (add == 0){
            return false ;
        }else {
            return true ;
        }
    }

    @Override
    public Set<AIMUserInfo> onlineUser() {
        Set<AIMUserInfo> set = null;
        // 返回LOGIN_STATUS_PREFIX 这个set 中所有的值
        Set<String > members = redisTemplate.opsForSet().members(LOGIN_STATUS_PREFIX);
//        member 就是用户的ID
        for(String member : members){
            if(set == null){
                set = new HashSet<>(64);
            }
            AIMUserInfo aimUserInfo = loadUserInfoByUserId(Long.valueOf(member));
            set.add(aimUserInfo);
        }
        return set;
    }
}
