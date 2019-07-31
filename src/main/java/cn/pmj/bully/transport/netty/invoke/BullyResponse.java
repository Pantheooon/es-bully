package cn.pmj.bully.transport.netty.invoke;

import cn.pmj.bully.cluster.node.NodeInfo;
import lombok.Data;

@Data
public class BullyResponse {

    private String requestId;

    private NodeInfo nodeInfo;

    private ResponseType msgType;
}
