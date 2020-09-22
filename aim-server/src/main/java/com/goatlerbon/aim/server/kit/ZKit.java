package com.goatlerbon.aim.server.kit;

import com.goatlerbon.aim.server.config.AppConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * zookeeper 工具
 */
@Component
public class ZKit {
    private static Logger logger = LoggerFactory.getLogger(ZKit.class);

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private AppConfiguration appConfiguration;

    /**
     * 在zk中创建父节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(appConfiguration.getZkRoot());
        System.out.println(exists);
        if(exists){
            return ;
        }
        //创建 持久结点
        zkClient.createPersistent(appConfiguration.getZkRoot());
    }

    /**
     * 创建临时结点
     * @param path
     */
    public void createNode(String path){
        zkClient.createEphemeral(path);
    }
}
