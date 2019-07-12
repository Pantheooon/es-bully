package cn.pmj.bully.node;

import lombok.Data;

import java.util.Objects;

@Data
public class NodeInfo {


    private String host;


    private Integer port;


    private String nodeId;


    private String version;


    protected NodeType nodeType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return Objects.equals(getNodeId(), nodeInfo.getNodeId());
    }

}
