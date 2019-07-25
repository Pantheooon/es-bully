package cn.pmj.bully.transport.netty.serialize.core;

public interface ISerialize {


    <T> byte[] encode(T o);


    <T> T  decode(byte[] bytes,Class<T> tClass);

}
