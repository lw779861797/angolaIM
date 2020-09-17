package com.goatlerbon.aim.client.init;

import com.goatlerbon.aim.client.handle.AIMClientHandle;
import com.goatlerbon.aim.common.protocol.AIMResponseProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class AIMClientHandleInitializer extends ChannelInitializer<Channel> {

    private final AIMClientHandle handle = new AIMClientHandle();

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(new IdleStateHandler(0,10,0))

                // google Protobuf 编解码
                //拆包解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(AIMResponseProto.AIMResProtocol.getDefaultInstance()))
                //拆包编码 固定套路
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(handle);
    }
}
