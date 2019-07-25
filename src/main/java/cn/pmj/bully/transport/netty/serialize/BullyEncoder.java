package cn.pmj.bully.transport.netty.serialize;

import cn.pmj.bully.transport.netty.serialize.core.ISerialize;
import cn.pmj.bully.transport.netty.serialize.core.JsonSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class BullyEncoder extends MessageToByteEncoder {

    private ISerialize serialize = new JsonSerialize();
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        int lengthPos = out.writerIndex();
        out.writeBytes(LENGTH_PLACEHOLDER);
        byte[] encode = serialize.encode(msg);
        out.writeBytes(encode);
        out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
    }
}
