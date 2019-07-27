package cn.pmj.bully.transport.discovery;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.InvokeFuture;
import cn.pmj.bully.transport.netty.invoke.ResponseHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
@Data
public class DiscoveryChannel {


    private NodeInfo nodeInfo;

    private Channel channel;

    private Long timeOut = 2L;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public DiscoveryChannel(Channel channel) {
        this.channel = channel;
    }


    public InvokeFuture writeAndFlush(BullyRequest bullyRequest) {
        channel.writeAndFlush(bullyRequest);
        InvokeFuture invokeFuture = new InvokeFuture(bullyRequest, timeOut, timeUnit);
        ResponseHolder.setFuture(invokeFuture);
        return invokeFuture;
    }




    public InvokeFuture ping(){
        BullyRequest request = new BullyRequest();
        return writeAndFlush(request);
    }



    public void close() {
        channel.close();
    }

}
