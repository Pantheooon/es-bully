package cn.pmj.bully.transport.netty.serialize;

public interface ISerialize {



    byte[] encode(Object o);


    Object decode(byte[] bytes);

}
