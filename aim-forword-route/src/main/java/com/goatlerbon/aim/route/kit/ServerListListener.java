package com.goatlerbon.aim.route.kit;

import com.goatlerbon.aim.route.config.AppConfiguration;
import com.goatlerbon.aim.route.util.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目启动时 加载该初始化线程
 */
public class ServerListListener implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ServerListListener.class);

    private ZKit zkUtil;

    private AppConfiguration appConfiguration ;


    public ServerListListener() {
        zkUtil = SpringBeanFactory.getBean(ZKit.class) ;
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class) ;
    }

    @Override
    public void run() {
        //注册监听服务
        zkUtil.subscribeEvent(appConfiguration.getZkRoot());

    }
}
