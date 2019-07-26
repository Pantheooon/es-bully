package cn.pmj.bully.cluster.node;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.util.Generator;
import lombok.Data;
import lombok.Generated;

import java.util.Objects;

@Data
public class NodeInfo {


    private String host;


    private Integer port;


    private String nodeId;

    private Role role;

    private Integer id ;




    public NodeInfo(Configuration configuration) {
        id = Generator.nodeId();
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return Objects.equals(getNodeId(), nodeInfo.getNodeId());
    }



   public   enum  Role{
        MASTER,SLAVE,NONE
    }

}
