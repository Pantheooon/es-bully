package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.invoke.RequestType;
import cn.pmj.bully.transport.netty.invoke.ResponseType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import static cn.pmj.bully.transport.netty.invoke.ResponseType.ACTIVE;
import static cn.pmj.bully.transport.netty.invoke.ResponseType.JOIN_SUCCESS;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private NodeInfo nodeInfo;

    public ServerHandler(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
                return electHandler();
            case ACTIVE:
                return activeHandler();
            case JOIN:
                return joinHandler();
            default:
                return null;
        }
    }

    private BullyHandler joinHandler() {
        return (request)->{
            BullyResponse response = new BullyResponse();
            response.setRequestId(request.getRequestId());
            response.setMsgType(JOIN_SUCCESS);
            return response;
        };
    }

    private BullyHandler activeHandler() {
        return (request)->{
            BullyResponse response = new BullyResponse();
            response.setRequestId(request.getRequestId());
            response.setMsgType(ACTIVE);
            response.setNodeInfo(nodeInfo);
            return response;
        };
    }

    private BullyHandler electHandler(){
        return (bullyRequest )->{
            BullyResponse response = new BullyResponse();
            response.setRequestId(bullyRequest.getRequestId());
            ClusterState clusterState = nodeInfo.getClusterState();
            Long currentVersion = clusterState.getElectVersion();
            Long electVersion = bullyRequest.getElectVersion();
            if (electVersion > clusterState.getElectVersion()) {
                response.setMsgType(ResponseType.PONG);
            } else if (electVersion < currentVersion) {
                response.setMsgType(ResponseType.FAILED);
                return response;
            } else {
                response.setMsgType(ResponseType.PONG);
                clusterState.setElectVersion(electVersion);
            }
            response.setNodeInfo(nodeInfo);
            return response;
        };
    }


    @FunctionalInterface
    public interface BullyHandler {


        BullyResponse handle(BullyRequest bullyRequest);
    }
}
