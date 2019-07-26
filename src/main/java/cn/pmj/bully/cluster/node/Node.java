package cn.pmj.bully.cluster.node;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.netty.DiscoveryServer;
import cn.pmj.bully.transport.discovery.DiscoveryChannel;
import cn.pmj.bully.transport.netty.invoke.InvokeFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Data
@Slf4j
public class Node {


    private List<NodeInfo> nodeList;
    private Configuration configuration;
    private NodeInfo localNodeInfo;
    private List<NodeInfo> activeNodes = new ArrayList<>();
    private Map<String, DiscoveryChannel> channelMap = new ConcurrentHashMap<>();
    private final static int RETRY_TIMES = 3;
    private ExecutorService startUp = Executors.newSingleThreadExecutor();


    public Node(NodeInfo nodeInfo, Configuration configuration) {
        localNodeInfo = nodeInfo;
        this.configuration = configuration;
    }


    public void put(DiscoveryChannel discoveryChannel) {
        channelMap.putIfAbsent(discoveryChannel.getNodeInfo().getNodeId(), discoveryChannel);
    }

    /**
     * @param nodeId
     */
    public void remove(String nodeId) {
        DiscoveryChannel discoveryChannel = channelMap.get(nodeId);
        NodeInfo nodeInfo = discoveryChannel.getNodeInfo();
        if (nodeInfo.getRole() == NodeInfo.Role.MASTER && checkMasterNodeOffLine()) {
        }

    }


    /**
     * 如果当前节点是node节点,检测到master下线,询问其他节点,如果超过半数都检测到master下线,则重新选举
     *
     * @return
     */
    public boolean checkMasterNodeOffLine() {
        return false;
    }

    /**
     * 如果当前节点是master节点,检测到slave下线,需要移除slave,并广播到cluster中
     */
    public void removeSlaveNodeThenBroadCast() {
    }

    public void startElect() {
        connectToOtherNode();
        while (!enoughNodeNumCheck()) {
            log.warn("node num at least need :{},but found:{}", configuration.getQuorum(), channelMap.size());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        NodeInfo master = findMaster();
        if (master.equals(localNodeInfo)) {
            pendingElectedMaster();
        } else {
            joinElectedMaster();
        }
    }

    private void connectToOtherNode() {
        startUp.execute(() -> {
            for (NodeInfo nodeInfo : nodeList) {
                DiscoveryChannel channel = null;
                try {
                    while ((channel = TcpClient.connect(localNodeInfo,nodeInfo)) != null) {
                        channel.setTimeOut(2l);
                        channel.setTimeUnit(TimeUnit.SECONDS);
                        put(channel);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public List<BullyResponse> elect() {
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


    public void bind(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>> listener) {
        DiscoveryServer server = new DiscoveryServer(getLocalNodeInfo(), listener);
        startUp.execute(server);
    }


    private boolean enoughNodeNumCheck() {
        return channelMap.size() >= configuration.getQuorum();
    }

    private void joinElectedMaster() {
    }

    private void pendingElectedMaster() {
    }

    private NodeInfo findMaster() {
        for (DiscoveryChannel value : channelMap.values()) {
//            InvokeFuture invokeFuture = value.writeAndFlush();

        }
        return null;
    }
}
