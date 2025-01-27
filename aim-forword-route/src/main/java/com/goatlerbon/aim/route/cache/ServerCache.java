package com.goatlerbon.aim.route.cache;

import com.goatlerbon.aim.route.kit.ZKit;
import com.google.common.cache.LoadingCache;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 从缓存中获取所有的服务列表
     *
     * @return
     */
    public List<String > getServerList(){
        List<String > list = new ArrayList<>();
        if(cache.size() == 0){
            List<String > allNode = zkUtil.getAllNode();
            for(String node : allNode){
                String key = node.split("-")[1];
                addCache(key);
            }
        }
        for (Map.Entry<String, String> entry : cache.asMap().entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    /**
     * rebuild cache list
     */
    public void rebuildCacheList(){
        updateCache(getServerList()) ;
    }
}
