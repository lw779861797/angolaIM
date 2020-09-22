package com.goatlerbon.aim.server.kit;

import com.goatlerbon.aim.server.config.AppConfiguration;
import com.goatlerbon.aim.server.util.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在容器启动时 向zk做注册自己的操作
 */
public class RegistryZK implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(RegistryZK.class);

    private ZKit zKit;

    private AppConfiguration appConfiguration ;

    private String ip;

    private int aimServerPort;

    private int httpPort;

    public RegistryZK(String addr, int aimServerPort, int port) {
        this.ip = addr;
        this.aimServerPort = aimServerPort;
        this.httpPort = port ;
        this.zKit = SpringBeanFactory.getBean(ZKit.class) ;
        this.appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class) ;
    }

    @Override
    public void run() {
        //创建zk中的父节点
        zKit.createRootNode();
//        是否要将自己注册到zk中
        if(appConfiguration.isZkSwitch()){
            String path = appConfiguration.getZkRoot() + "/ip-" + ip + ":" + aimServerPort + ":" + httpPort;
            zKit.createNode(path);
            logger.info("Registry zookeeper success, msg=[{}]", path);
        }
    }
}
