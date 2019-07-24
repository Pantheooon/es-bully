package cn.pmj.bully.transport.netty.serialize;

import cn.pmj.bully.transport.netty.serialize.core.ISerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class BullyDecoder<T> extends LengthFieldBasedFrameDecoder {


    private ISerialize serialize = new JsonSerialize();

    private Class<T> tClass;


    public BullyDecoder(Class<T> tClass) {
        super(1048576, 0, 4, 0, 4);
        this.tClass = tClass;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        int length = frame.readableBytes();
        byte[] bytes = new byte[length];
        frame.getBytes(0, bytes);
        return serialize.decode(bytes,tClass);
    }
}
