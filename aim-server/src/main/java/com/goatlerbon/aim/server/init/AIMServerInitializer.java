package com.goatlerbon.aim.server.init;

import com.goatlerbon.aim.common.protocol.AIMRequestProto;
import com.goatlerbon.aim.server.handle.AIMServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class AIMServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                //增加心跳支持
//               针对客户端，如果在x分钟时没有向服务端发送读写的心跳，则主动断开(all)
                //11 秒 用户没有向客户端发送消息就发生心跳断开
                .addLast(new IdleStateHandler(11,0,0))
                //使用protobuf作为序列化的方式
                /**
                 * Xml、Json是目前常用的数据交换格式，它们直接使用字段名称维护序列化后类实例中字段与数据之间的映射关系，
                 * 一般用字符串的形式保存在序列化后的字节流中。消息和消息的定义相对独立，可读性较好。
                 * 但序列化后的数据字节很大，序列化和反序列化的时间较长，数据传输效率不高。
                 *
                 *  Protobuf和Xml、Json序列化的方式不同，采用了二进制字节的序列化方式，
                 *  用字段索引和字段类型通过算法计算得到字段之前的关系映射，从而达到更高的时间效率和空间效率，
                 *  特别适合对数据大小和传输速率比较敏感的场合使用。
                 */
//                用于处理半包问题 google Protobuf 解码
//        https://www.cnblogs.com/yinbiao/p/11015039.html 拆包和粘包问题
                .addLast(new ProtobufVarint32FrameDecoder())
//                告诉ProtobufDecoder 要解码的目标类是什么
                .addLast(new ProtobufDecoder(AIMRequestProto.AIMReqProtocol.getDefaultInstance()))
//                  对protobuf协议的的消息头上加上一个长度为32的整形字段，用于标志这个消息的长度。
//                用于解决粘包问题
                .addLast(new ProtobufVarint32LengthFieldPrepender())
//                  google Protobuf 编码
                .addLast(new ProtobufEncoder())
                .addLast(new AIMServerHandler());
    }
}
