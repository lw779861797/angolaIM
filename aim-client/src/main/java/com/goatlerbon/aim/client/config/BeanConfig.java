package com.goatlerbon.aim.client.config;

import com.goatlerbon.aim.common.data.construct.RingBufferWheel;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class BeanConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(BeanConfig.class);

    @Value("${aim.user.id}")
    private long userId;

    @Value("${aim.callback.thread.queue.size}")
    private int queueSize;

    @Value("${aim.callback.thread.pool.size}")
    private int poolSize;

    /**
     * http client
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    /**
     * 创建回调线程池
     */
    @Bean("callBackThreadPool")
    public ThreadPoolExecutor buildCallerThread(){
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        ThreadFactory product = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("msg-callback-%d")
                .build();
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolSize,poolSize,1,TimeUnit.MILLISECONDS,queue,product);
        return poolExecutor;
    }

    @Bean
    public RingBufferWheel bufferWheel(){
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        return new RingBufferWheel(executorService);
    }
}
