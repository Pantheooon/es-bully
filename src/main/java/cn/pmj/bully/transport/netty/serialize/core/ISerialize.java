package cn.pmj.bully.transport.netty.serialize.core;

import cn.pmj.bully.transport.netty.BullyRequest;
public interface ISerialize {


    <T> byte[] encode(T o);


    <T> T  decode(byte[] bytes,Class<T> tClass);

}
