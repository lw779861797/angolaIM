package com.goatlerbon.aim.common.util;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.pojo.RouteInfo;

public class RouteInfoParseUtil {

    /**
     *
     * @param info 服务器地址 这个地址 包含了一些集成的信息 需要 解析出IP地址
     * @return
     */
    public static RouteInfo parse(String info) {
        try {
            String[] serverInfo = info.split(":");
            RouteInfo routeInfo = new RouteInfo(serverInfo[0],Integer.parseInt(serverInfo[1]),Integer.parseInt(serverInfo[2]));
            return routeInfo;
        }catch (Exception e){
            throw new AIMException(StatusEnum.VALIDATION_FAIL);
        }

    }
}
