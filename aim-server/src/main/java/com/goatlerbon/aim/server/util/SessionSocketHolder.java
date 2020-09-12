package com.goatlerbon.aim.server.util;

import com.goatlerbon.aim.common.pojo.AIMUserInfo;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户连接工具类
 */
public class SessionSocketHolder {
    /**
     * 存储对应 用户的channel映射 id - channel
     */
    private static final Map<Long, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);

    /**
     * 存储用户信息 id - username
     */
    private static final Map<Long,String> SESSION_MAP = new ConcurrentHashMap<>(16);

    public static void saveSession(Long userId,String userName){
        SESSION_MAP.put(userId,userName);
    }

    public static void removeSession(Long userId){
        SESSION_MAP.remove(userId);
    }

    public static void put(Long userId,NioSocketChannel socketChannel){
        CHANNEL_MAP.put(userId,socketChannel);
    }

    public static NioSocketChannel get(Long id) {
        return CHANNEL_MAP.get(id);
    }

    public static Map<Long, NioSocketChannel> getRelationShip() {
        return CHANNEL_MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }

    public static AIMUserInfo getUserId(NioSocketChannel socketChannel){
        for(Map.Entry<Long,NioSocketChannel> entry : CHANNEL_MAP.entrySet()){
            NioSocketChannel value = entry.getValue();
            if(socketChannel == value){
                Long key = entry.getKey();
                String userName = SESSION_MAP.get(key);
                AIMUserInfo userInfo = new AIMUserInfo(key,userName);
                return userInfo;
            }
        }
        return null;
    }
}
