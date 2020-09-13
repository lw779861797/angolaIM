package com.goatlerbon.aim.common.route.algorithm;

import java.util.List;

/**
 * 路由 处理器 怎么选择路由
 * 通过一些负载均衡的算法 可以从 多台服务器中 选出 使用的服务器
 * 负载均衡算法有：
 * 轮询 随机 一致性hash算法等等
 */
public interface RouteHandle {
    /**
     * 在一批服务器里进行路由
     * @param values
     * @param key
     * @return
     */
    String routeServer(List<String> values, String key) ;
}
