package com.goatlerbon.aim.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public ZkClient buildZkClient(){
        return new ZkClient(appConfiguration.getZkAddr(),appConfiguration.getZkConnectTimeout());
    }
}
