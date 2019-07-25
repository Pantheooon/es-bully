package cn.pmj.bully.transport.discovery.handler;

import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;

public interface BullyHandler {



    BullyResponse handle(BullyRequest bullyRequest);
}
