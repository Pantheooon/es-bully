package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.discovery.DiscoveryChannel;
import cn.pmj.bully.transport.netty.invoke.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private NodeInfo localNodeInfo;
    private Node node;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public ClientHandler(NodeInfo localNodeInfo, Node node) {
        this.localNodeInfo = localNodeInfo;
        this.node = node;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        BullyRequest request = new BullyRequest();
        request.setNodeInfo(localNodeInfo);
        request.setType(RequestType.ACTIVE);
        ctx.channel().writeAndFlush(request);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BullyResponse bullyResponse = (BullyResponse) msg;
        if (bullyResponse.getMsgType() == ResponseType.ACTIVE) {
            DiscoveryChannel discoveryChannel = new DiscoveryChannel(localNodeInfo, bullyResponse.getNodeInfo(), ctx.channel());
            node.put(discoveryChannel);
        }
        ResponseHolder.setResponse(bullyResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
