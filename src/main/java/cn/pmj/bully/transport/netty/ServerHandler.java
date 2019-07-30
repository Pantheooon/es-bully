package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.discovery.handler.BullyHandler;
import cn.pmj.bully.transport.discovery.handler.ElectHandler;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.invoke.RequestType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import static cn.pmj.bully.transport.netty.invoke.ResponseType.ACTIVE;

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
        BullyHandler handler = handler(request.getType());
        ctx.writeAndFlush(handler.handle(request));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private BullyHandler handler(RequestType type) {
        switch (type) {
            case ELECT:
                return new ElectHandler(this.nodeInfo);
            case ACTIVE:
                return activeHandler();
            default:
                return null;
        }
    }

    private BullyHandler activeHandler() {
        return new BullyHandler() {
            @Override
            public BullyResponse handle(BullyRequest bullyRequest) {
                BullyResponse response = new BullyResponse();
                response.setRequestId(bullyRequest.getRequestId());
                response.setMsgType(ACTIVE);
                response.setNodeInfo(nodeInfo);
                return response;
            }
        };
    }
}
