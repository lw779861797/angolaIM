package com.goatlerbon.aim.server.kit;

import com.goatlerbon.aim.common.core.proxy.ProxyManager;
import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import com.goatlerbon.aim.route.api.RouteApi;
import com.goatlerbon.aim.route.api.vo.req.ChatReqVo;
import com.goatlerbon.aim.server.config.AppConfiguration;
import com.goatlerbon.aim.server.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 路由处理
 */
@Component
public class RouteHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(RouteHandler.class);

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private AppConfiguration configuration;

    /**
     * 用户下线
     */
    public void userOffLine(AIMUserInfo userInfo, NioSocketChannel channel){
        if(userInfo != null){
            LOGGER.info("Account [{}] offline", userInfo.getUserName());
            SessionSocketHolder.removeSession(userInfo.getUserId());
//            清除路由关系
            clearRouteInfo(userInfo);
        }
        SessionSocketHolder.remove(channel);
    }

    /**
     * 清除路由关系
     * @param userInfo
     */
    public void clearRouteInfo(AIMUserInfo userInfo) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class,configuration.getRouteUrl(),okHttpClient).getInstance();
        Response response = null;
        ChatReqVo vo = new ChatReqVo(userInfo.getUserId(),userInfo.getUserName());
        try {
            response = (Response) routeApi.offLine(vo);
        } catch (Exception e){
            LOGGER.error("Exception",e);
        }finally {
            response.body().close();
        }
    }
}
