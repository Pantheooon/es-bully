package cn.pmj.bully.transport.discovery.handler;

import cn.pmj.bully.transport.netty.BullyRequest;
import cn.pmj.bully.transport.netty.BullyResponse;

public interface BullyHandler {



    BullyResponse handle(BullyRequest bullyRequest);
}
