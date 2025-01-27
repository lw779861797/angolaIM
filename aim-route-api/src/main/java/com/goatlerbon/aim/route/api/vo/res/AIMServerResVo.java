package com.goatlerbon.aim.route.api.vo.res;

import com.goatlerbon.aim.common.pojo.RouteInfo;

import java.io.Serializable;

public class AIMServerResVo implements Serializable {

    private String ip ;
//    netty服务器端口
    private Integer aimServerPort;
//    服务器端口
    private Integer httpPort;

    public AIMServerResVo(RouteInfo routeInfo) {
        this.ip = routeInfo.getIp();
        this.aimServerPort = routeInfo.getAimServerPort();
        this.httpPort = routeInfo.getHttpPort();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getAimServerPort() {
        return aimServerPort;
    }

    public void setAimServerPort(Integer aimServerPort) {
        this.aimServerPort = aimServerPort;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }
}
