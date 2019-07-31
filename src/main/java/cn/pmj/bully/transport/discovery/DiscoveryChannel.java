package cn.pmj.bully.transport.discovery;

import cn.pmj.bully.cluster.ElectCallBack;
import cn.pmj.bully.cluster.node.INode;
import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.*;
import cn.pmj.bully.util.Generator;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static cn.pmj.bully.transport.netty.invoke.RequestType.JOIN;


@Slf4j
@Data
public class DiscoveryChannel {


    private NodeInfo local;

    private NodeInfo dest;

    private Channel channel;

    private Long timeOut = 2L;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public DiscoveryChannel(NodeInfo local, NodeInfo dest, Channel channel) {
        this.local = local;
        this.channel = channel;
        this.dest = dest;
    }


    public InvokeFuture writeAndFlush(BullyRequest bullyRequest) {
        channel.writeAndFlush(bullyRequest);
        InvokeFuture invokeFuture = new InvokeFuture(bullyRequest, timeOut, timeUnit);
        ResponseHolder.setFuture(invokeFuture);
        return invokeFuture;
    }


    public InvokeFuture elect() {
        BullyRequest request = new BullyRequest();
        request.setType(RequestType.ELECT);
        request.setNodeInfo(local);
        request.setRequestId(Generator.requestId());
        request.setElectVersion(local.getClusterState().getElectVersion());
        return writeAndFlush(request);
    }


    public void close() {
        channel.close();
    }

    public void joinCluster(NodeInfo master, Node node, ElectCallBack electCallBack) {
        BullyRequest request = new BullyRequest();
        request.setType(JOIN);
        request.setNodeInfo(local);
        request.setElectVersion(local.getClusterState().getElectVersion());
        request.setMaster(master);
        InvokeFuture future = writeAndFlush(request);
        try {
            BullyResponse response = future.get();
            NodeInfo nodeInfo = response.getNodeInfo();
            node.addJoinedNodeInfo(nodeInfo);
        } catch (Exception e) {
            electCallBack.failed(e);
        }
        electCallBack.success();
    }
}
