package cn.pmj.bully.transport.netty.serialize;

public class MessagePackSerialize implements ISerialize {
    @Override
    public byte[] encode(Object o) {
        return new byte[0];
    }

    @Override
    public Object decode(byte[] bytes) {
        return null;
    }
}
