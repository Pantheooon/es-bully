package cn.pmj.bully.cluster.node;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.netty.DiscoveryServer;
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
    private NodeInfo localNodeInfo;
    private List<NodeInfo> activeNodes = new ArrayList<>();

    private final static int RETRY_TIMES = 3;

    public Node(NodeInfo nodeInfo, Configuration configuration) {
        localNodeInfo = nodeInfo;
        this.configuration = configuration;
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
        TcpClient client = new TcpClient(nodeInfo);
        try{
            DiscoveryPing ping = null;
            try {
                ping = client.connect(RETRY_TIMES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ping.ping();
        }finally {
            client.shutDownGracefully();
        }

    }


    public void bind() {
        try {
            DiscoveryServer server = new DiscoveryServer(getLocalNodeInfo());
            server.doBind((future) -> {
                if (future.isSuccess()) {
                    log.info("node:{},bind port:{},successfully", getLocalNodeInfo().getNodeId(), getLocalNodeInfo().getPort());
                } else {
                    log.info("node server started faile ,cause:{}", future.cause().getLocalizedMessage());
                }
            });
        } catch (InterruptedException e) {
            log.error("start failed-->{}", e.getLocalizedMessage());
        }
    }


}
