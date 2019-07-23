package cn.pmj.bully.transport.netty.serialize.core;

import cn.pmj.bully.transport.netty.BullyRequest;
import cn.pmj.bully.transport.netty.BullyResponse;

public interface ISerialize {


    byte[] encode(BullyRequest o);


    BullyResponse decode(byte[] bytes);

}
