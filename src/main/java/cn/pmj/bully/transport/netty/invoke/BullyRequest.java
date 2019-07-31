package cn.pmj.bully.transport.netty.invoke;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.util.Generator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BullyRequest {


    private String requestId = Generator.requestId();

    private NodeInfo nodeInfo;

    private Long electVersion;

    private RequestType type;

    private NodeInfo master;



}
