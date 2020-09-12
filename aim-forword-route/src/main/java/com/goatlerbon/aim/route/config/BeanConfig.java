package com.goatlerbon.aim.route.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class BeanConfig {
    private static Logger logger = LoggerFactory.getLogger(BeanConfig.class);

    @Autowired
    private AppConfiguration appConfiguration;

    /**
     * ZK客户端
     * @return
     */
    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(appConfiguration.getZkAddr(), appConfiguration.getZkConnectTimeout());
    }

    /**
     * 本地缓存 guava cache
     * @return
     */
    @Bean
    public LoadingCache<String, String> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {

                    //在load方法中定义value的加载方法；
//                    如果没有找到缓存时返回null
                    @Override
                    public String load(String s) throws Exception {
                        return null;
                    }
                });
    }

    /**
     * RedisTemplate
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        初始化参数和初始化工作
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
