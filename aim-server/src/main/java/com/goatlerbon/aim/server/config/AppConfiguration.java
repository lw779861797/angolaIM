package com.goatlerbon.aim.server.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfiguration {
    //zk 注册根节点
    @Value("${app.zk.root}")
    private String zkRoot;

    //zk 地址
    @Value("${app.zk.addr}")
    private String zkAddr;

    //是否注册 zk
    @Value("${app.zk.switch}")
    private boolean zkSwitch;

//    aim 服务器端口
    @Value("${aim.server.port}")
    private int aimServerPort;

    @Value("${aim.route.url}")
    private String routeUrl;

//    检测多少秒没有收到客户端心跳后服务端关闭连接 单位秒
    @Value("${aim.heartbeat.time}")
    private long heartBeatTime;

//    zk 连接超时时限
    @Value("${app.zk.connect.timeout}")
    private int zkConnectTimeout;

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public boolean isZkSwitch() {
        return zkSwitch;
    }

    public void setZkSwitch(boolean zkSwitch) {
        this.zkSwitch = zkSwitch;
    }

    public int getAimServerPort() {
        return aimServerPort;
    }

    public void setAimServerPort(int aimServerPort) {
        this.aimServerPort = aimServerPort;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    public long getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public int getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public void setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }
}
