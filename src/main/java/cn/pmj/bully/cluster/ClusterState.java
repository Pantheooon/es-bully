package cn.pmj.bully.cluster;

import cn.pmj.bully.cluster.node.NodeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterState {


    private Map<String,NodeInfo> activeNodeInfo = new ConcurrentHashMap<>();

    private NodeInfo master;

    private Long electVersion = 0L;


    public void addActiveNodeInfo(String nodeId, NodeInfo dest) {
        activeNodeInfo.putIfAbsent(nodeId,dest);
    }
}
