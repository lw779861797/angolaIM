package com.goatlerbon.aim.common.route.algorithm.random;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.route.algorithm.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 路由策略为随机
 */
public class RandomHandle implements RouteHandle {
    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if(size == 0){
            //没有服务器存在
            throw new AIMException(StatusEnum.SERVER_NOT_AVAILABLE);
        }
        int offset = ThreadLocalRandom.current().nextInt(size);
        return values.get(offset);
    }
}
