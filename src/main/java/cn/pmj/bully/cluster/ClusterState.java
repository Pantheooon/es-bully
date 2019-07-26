package cn.pmj.bully.cluster;

import com.sun.org.apache.xalan.internal.lib.NodeInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ClusterState {


    private List<NodeInfo> nodeInfos = new ArrayList<>();

    private NodeInfo master;

    private Long timestamp;

    private Long electVersion;

}
