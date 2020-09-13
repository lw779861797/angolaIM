package com.goatlerbon.aim.route.cache;

import com.goatlerbon.aim.route.kit.ZKit;
import com.google.common.cache.LoadingCache;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 服务器结点缓存 本地缓存 guava cache
 */
@Component
public class ServerCache {
    private static Logger logger = LoggerFactory.getLogger(ServerCache.class) ;

    @ApiModelProperty("本地缓存")
    @Autowired
    private LoadingCache<String, String> cache;

    @Autowired
    private ZKit zkUtil;

    public void addCache(String key) {
        cache.put(key, key);
    }

    /**
     * 更新所有缓存/   先删除 再新增
     * @param currentChildren
     */
    public void updateCache(List<String> currentChildren) {
//        删除所有缓存
        cache.invalidateAll();
        for(String currentChild : currentChildren){
            // currentChildren=ip-127.0.0.1:11212:9082 or 127.0.0.1:11212:9082
            String key ;
            if (currentChild.split("-").length == 2){
                key = currentChild.split("-")[1];
            }else {
                key = currentChild ;
            }
//            存入缓存
            addCache(key);
        }
    }
}
