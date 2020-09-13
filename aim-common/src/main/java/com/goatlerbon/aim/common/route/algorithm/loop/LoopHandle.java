package com.goatlerbon.aim.common.route.algorithm.loop;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.route.algorithm.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 路由策略为 轮询
 */
public class LoopHandle implements RouteHandle {
    private AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values, String key) {
        if (values.size() == 0) {
            throw new AIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
        Long position = index.incrementAndGet() % values.size();
        if(position < 0){
            position = 0L;
        }
        return values.get(position.intValue());
    }
}
