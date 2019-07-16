package cn.pmj.bully.transport.ping;

import cn.pmj.bully.cluster.node.NodeInfo;
import lombok.Data;

import java.nio.channels.Channel;
import java.util.concurrent.Future;

public class DiscoveryPing {


    private NodeInfo nodeInfo;


    private Channel channel;


    public Future<DiscoveryPingResponse> ping() {
        return null;
    }


    @Data
    public class DiscoveryPingResponse {


        private String nodeId;

        private String masterId;

        private Long version;
    }


}
