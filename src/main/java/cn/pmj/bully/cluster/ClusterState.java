package cn.pmj.bully.cluster;

import cn.pmj.bully.cluster.node.NodeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterState {


    private List<NodeInfo> nodeInfos = new ArrayList<>();

    private NodeInfo master;

    private Long electVersion;

}
