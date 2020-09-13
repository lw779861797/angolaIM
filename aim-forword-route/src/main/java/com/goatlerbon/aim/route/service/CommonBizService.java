package com.goatlerbon.aim.route.service;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.pojo.RouteInfo;
import com.goatlerbon.aim.route.cache.ServerCache;
import com.goatlerbon.aim.route.kit.NetAddressIsReachable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonBizService {
    private static Logger logger = LoggerFactory.getLogger(CommonBizService.class) ;

    @Autowired
    private ServerCache serverCache;

    /**
     * 检查 IP 和端口是否正确
     * @param routeInfo
     */
    public void checkServerAvailable(RouteInfo routeInfo){
        //先尝试连接
        boolean reachable = NetAddressIsReachable.checkAddressReachable(routeInfo.getIp(),routeInfo.getAimServerPort(),1000);
        if(!reachable){
            logger.error("ip={}, port={} are not available", routeInfo.getIp(), routeInfo.getAimServerPort());

            // 重新获取 zk上 服务的信息列表
            serverCache.rebuildCacheList();
            throw new AIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
    }
}
