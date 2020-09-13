package com.goatlerbon.aim.route.config;

import com.goatlerbon.aim.common.route.algorithm.RouteHandle;
import com.goatlerbon.aim.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import okhttp3.OkHttpClient;
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

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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

    /**
     * http client
     *
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    /**
     * 根据配置文件中 使用的负载均衡算法类 选择
     * @return
     * @throws Exception
     */
    @Bean
    public RouteHandle buildRouteHandle() throws Exception{
        String routeWay = appConfiguration.getRouteWay();
        RouteHandle routeHandle = (RouteHandle) Class.forName(routeWay).newInstance();
        logger.info("Current route algorithm is [{}]", routeHandle.getClass().getSimpleName());
        //如果 在配置文件中 负载均衡策略 选择 一致性hash算法
        if(routeWay.contains("ConsistentHash")){
            //如果是 一致性hash算法 则需要提供 该算法的具体实现
            Method method = Class.forName(routeWay).getMethod("setHash", AbstractConsistentHash.class);
            AbstractConsistentHash consistentHash =
                    (AbstractConsistentHash) Class.forName(appConfiguration.getConsistentHashWay()).newInstance();
            method.invoke(routeHandle,consistentHash);
            return routeHandle;
        }else {
            return routeHandle;
        }
    }
}
