package com.goatlerbon.aim.server.config;

import com.goatlerbon.aim.common.constant.Constants;
import com.goatlerbon.aim.common.protocol.AIMRequestProto;
import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class BeanConfig {
    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public ZkClient buildZkClient(){
        return new ZkClient(appConfiguration.getZkAddr(),appConfiguration.getZkConnectTimeout());
    }

    @Bean(value = "heartBeat")
    public AIMRequestProto.AIMReqProtocol heartBeat(){
        AIMRequestProto.AIMReqProtocol heart = AIMRequestProto.AIMReqProtocol.newBuilder()
                .setRequestId(0L)
                .setReqMsg("pong")
                .setType(Constants.CommandType.PING)
                .build();
        return heart;
    }

    @Bean
    public OkHttpClient okHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
//                连接失败重试
                .retryOnConnectionFailure(true);
        return builder.build();
    }
}
