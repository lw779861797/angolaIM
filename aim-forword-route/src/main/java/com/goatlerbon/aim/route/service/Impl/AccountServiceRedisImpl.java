package com.goatlerbon.aim.route.service.Impl;

import com.goatlerbon.aim.common.core.proxy.ProxyManager;
import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.common.util.RouteInfoParseUtil;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.route.api.vo.req.LoginReqVo;
import com.goatlerbon.aim.route.api.vo.req.SimpleChatReqVo;
import com.goatlerbon.aim.route.api.vo.res.AIMServerResVo;
import com.goatlerbon.aim.route.api.vo.res.RegisterInfoResVo;
import com.goatlerbon.aim.route.service.AccountService;
import com.goatlerbon.aim.route.service.UserInfoCacheService;
import com.goatlerbon.aim.server.api.ServerApi;
import com.goatlerbon.aim.server.api.vo.req.SendMsgReqVo;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.goatlerbon.aim.route.constant.Constant.ACCOUNT_PREFIX;
import static com.goatlerbon.aim.route.constant.Constant.ROUTE_PREFIX;

@Service
public class AccountServiceRedisImpl implements AccountService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountServiceRedisImpl.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoCacheService userInfoCacheService;

    @Autowired
    OkHttpClient okHttpClient;

    @Override
    public void offLine(Long userId) throws Exception {
        // TODO: 2019-01-21 改为一个原子命令，以防数据不一致性 这块没明白

        //删除路由
        redisTemplate.delete(ROUTE_PREFIX + userId);

        //删除登录状态
        userInfoCacheService.removeLoginStatus(userId);
    }

    @Override
    public RegisterInfoResVo register(RegisterInfoResVo info) {
        //账号信息
        String key = ACCOUNT_PREFIX + info.getUserId();
        String name = (String) redisTemplate.opsForValue().get(info.getUserName());
        if(name == null){
            //为了方便查询，冗余一份
            redisTemplate.opsForValue().set(key, info.getUserName());
            redisTemplate.opsForValue().set(info.getUserName(), key);
        }else {
            long userId = Long.parseLong(name.split(":")[1]);
            info.setUserId(userId);
            info.setUserName(info.getUserName());
        }
        return info;
    }

    @Override
    public StatusEnum login(LoginReqVo loginReqVo) {
        //再去Redis里查询
        String key = ACCOUNT_PREFIX + loginReqVo.getUserId();
        String userName = (String) redisTemplate.opsForValue().get(key);
        if (null == userName) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        if (!userName.equals(loginReqVo.getUserName())) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }


        //登录成功，保存登录状态
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(loginReqVo.getUserId());
        if (status == false) {
            //重复登录
            return StatusEnum.REPEAT_LOGIN;
        }

        return StatusEnum.SUCCESS;
    }

    /**
     *
     * @param loginReqVo
     * @param msg 服务器IP
     */
    @Override
    public void saveRouteInfo(LoginReqVo loginReqVo, String msg) {
        String key = ROUTE_PREFIX + loginReqVo.getUserId();
        redisTemplate.opsForValue().set(key, msg);
    }

    @Override
    public AIMServerResVo loadRouteRelatedByUserId(Long receiveUserId) {
        String value = (String) redisTemplate.opsForValue().get(ROUTE_PREFIX + receiveUserId);
        if(value == null){
            //value 等于空说明 接受用户不在线
            throw new AIMException(StatusEnum.OFF_LINE);
        }

        AIMServerResVo aimServerResVo = new AIMServerResVo(RouteInfoParseUtil.parse(value));
        return aimServerResVo;
    }

    @Override
    public void pushMsg(AIMServerResVo serverResVo, Long sendUserId, ChatReqVo chatReqVo) {
//        获取用户信息
        AIMUserInfo userInfo = userInfoCacheService.loadUserInfoByUserId(sendUserId);

        String url = "http://" + serverResVo.getIp() + ":" + serverResVo.getHttpPort();
        //反射调用远程方法
        ServerApi serverApi = new ProxyManager<>(ServerApi.class, url, okHttpClient).getInstance();
        SendMsgReqVo vo = new SendMsgReqVo(userInfo.getUserName() + ":" + chatReqVo.getMsg(),chatReqVo.getUserId());
        Response response = null;
        try {
            response = (Response) serverApi.sendMsg(vo);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        } finally {
            response.body().close();
        }
    }
}
