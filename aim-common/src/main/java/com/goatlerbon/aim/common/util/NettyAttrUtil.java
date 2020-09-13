package com.goatlerbon.aim.common.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyAttrUtil {
    /**
     * 为channel 设置一个属性
     * 这个属性就是判断 客户端最后一次读写发生在什么时候
     */
    private static final AttributeKey<String> ATTR_KEY_READER_TIME = AttributeKey.valueOf("readerTime");

    public static void updateReaderTime(Channel channel,Long time){
        channel.attr(ATTR_KEY_READER_TIME).set(time.toString());
    }

    public static Long getReaderTime(Channel channel){
        String value = getAttribute(channel,ATTR_KEY_READER_TIME);
        if(value != null){
            return Long.valueOf(value);
        }
        return null;
    }

    private static String getAttribute(Channel channel, AttributeKey<String> key) {
        Attribute<String> attr = channel.attr(key);
        return attr.get();
    }
}
