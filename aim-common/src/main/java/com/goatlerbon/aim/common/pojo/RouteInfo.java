package com.goatlerbon.aim.common.pojo;

/**
 * 路由信息
 */
public class RouteInfo {

    private String ip ;

//    netty服务器端口
    private Integer aimServerPort;

//    服务器端口
    private Integer httpPort;

    public RouteInfo(String ip, Integer aimServerPort, Integer httpPort) {
        this.ip = ip;
        this.aimServerPort = aimServerPort;
        this.httpPort = httpPort;
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
