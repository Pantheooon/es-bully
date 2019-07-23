package cn.pmj.bully.cluster.node;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.transport.netty.BullyResponse;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.netty.DiscoveryServer;
import cn.pmj.bully.transport.ping.DiscoveryChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Data
@Slf4j
public class Node {


    private List<NodeInfo> nodeList;
    private Configuration configuration;
    private NodeInfo localNodeInfo;
    private List<NodeInfo> activeNodes = new ArrayList<>();
    private Map<String, DiscoveryChannel> uniCastMap = new ConcurrentHashMap<>();
    private final static int RETRY_TIMES = 3;

    public Node(NodeInfo nodeInfo, Configuration configuration) {
        localNodeInfo = nodeInfo;
        this.configuration = configuration;
    }


    public void connectToNodes() {
        for (NodeInfo nodeInfo : nodeList) {
            if (!nodeInfo.equals(localNodeInfo)) {
                try {
                    DiscoveryChannel connect = TcpClient.connect(nodeInfo);
                    uniCastMap.put(nodeInfo.getNodeId(), connect);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public List<BullyResponse> ping() {
        List<Future<BullyResponse>> futures = new ArrayList<>();
        for (NodeInfo nodeInfo : nodeList) {
        }

        if (futures != null && futures.size() != 0) {
            List<BullyResponse> responses = new ArrayList<>();
            for (Future<BullyResponse> future : futures) {
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


    //todo
    public void bind() {
        try {
            DiscoveryServer server = new DiscoveryServer(getLocalNodeInfo(), (future) -> {
                if (future.isSuccess()) {
                    log.info("node:{},bind port:{},successfully", getLocalNodeInfo().getNodeId(), getLocalNodeInfo().getPort());
                } else {
                    log.info("node server started faile ,cause:{}", future.cause().getLocalizedMessage());
                }
            });
            server.doBind();
        } catch (InterruptedException e) {
            log.error("start failed-->{}", e.getLocalizedMessage());
        }
    }


}
