package cn.pmj.bully.cluster.node;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.util.Generator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Objects;

@Data
public class NodeInfo implements INode {


    private String host;


    private Integer port;


    private String nodeId;

    private Role role;

    private Integer id;

    @JSONField(serialize = false)
    private Configuration configuration;

    private ClusterState clusterState;

    public NodeInfo(Configuration configuration) {
        this.host = configuration.getHost();
        this.port = configuration.getPort();
        this.nodeId = configuration.getNodeId();
        id = Generator.nodeId();
        clusterState = new ClusterState();
        this.configuration = configuration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return Objects.equals(getNodeId(), nodeInfo.getNodeId());
    }


    public enum Role {
        MASTER, SLAVE, NONE
    }

}
