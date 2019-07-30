package cn.pmj.bully.transport.discovery.handler;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.invoke.ResponseType;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ElectHandler implements BullyHandler {

    private NodeInfo nodeInfo;


    @Override
    public BullyResponse handle(BullyRequest bullyRequest) {
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
    }
}
