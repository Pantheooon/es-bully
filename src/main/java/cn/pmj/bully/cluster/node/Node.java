package cn.pmj.bully.cluster.node;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.transport.netty.channel.ChannelClient;
import cn.pmj.bully.transport.netty.channel.ChannelServer;
import cn.pmj.bully.transport.ping.DiscoveryPing;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Data
@Slf4j
public class Node {


    private List<NodeInfo> nodeList;
    private Map<String, DiscoveryPing> uniCastMap;
    private Configuration configuration;
    private NodeInfo localNode;
    private List<NodeInfo> activeNodes = new ArrayList<>();

    public Node(NodeInfo nodeInfo, Configuration configuration) {
        localNode = nodeInfo;
        this.configuration = configuration;
    }

    public void bind() {
        doBind();
    }


    public List<DiscoveryPing.DiscoveryPingResponse> ping() {
        List<Future<DiscoveryPing.DiscoveryPingResponse>> futures = new ArrayList<>();
        for (NodeInfo nodeInfo : nodeList) {
            futures.add(ping(nodeInfo));
        }

        if (futures != null && futures.size() != 0) {
            List<DiscoveryPing.DiscoveryPingResponse> responses = new ArrayList<>();
            for (Future<DiscoveryPing.DiscoveryPingResponse> future : futures) {
                try {
                    responses.add(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Future<DiscoveryPing.DiscoveryPingResponse> ping(NodeInfo nodeInfo) {
        try {
            DiscoveryPing ping = ChannelClient.connect(nodeInfo, 3);
            return ping.ping();
        } catch (InterruptedException e) {
            return null;
        }
    }


    private void doBind() {
        try {
            ChannelServer.connect(localNode);
        } catch (InterruptedException e) {
            log.error("start failed-->{}", e.getLocalizedMessage());
        }
    }


}
