package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private NodeInfo nodeInfo;

    public ServerHandler(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("msg-->{}", msg);
        BullyRequest request = (BullyRequest) msg;
        BullyResponse response = new BullyResponse();
        response.setRequestId(request.getRequestId());
        response.setResponse(JSON.toJSONString(request));
        ChannelFuture future = ctx.writeAndFlush(response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
