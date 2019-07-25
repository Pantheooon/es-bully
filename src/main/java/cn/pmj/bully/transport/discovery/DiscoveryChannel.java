package cn.pmj.bully.transport.discovery;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.BullyRequest;
import cn.pmj.bully.transport.netty.ResponseHolder;
import cn.pmj.bully.transport.netty.invoke.InvokeFuture;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
@Data
public class DiscoveryChannel {


    private NodeInfo nodeInfo;


    private Channel channel;

    private Long timeOut;

    private TimeUnit timeUnit;

    public DiscoveryChannel(Channel channel, Long timeOut, TimeUnit timeUnit) {
        this.channel = channel;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
    }


    public InvokeFuture writeAndFlush(BullyRequest bullyRequest) {
        channel.writeAndFlush(bullyRequest);
        InvokeFuture invokeFuture = new InvokeFuture(bullyRequest, timeOut, timeUnit);
        ResponseHolder.setFuture(invokeFuture);
        return invokeFuture;
    }


    public void close(){
        channel.close();
    }

}
