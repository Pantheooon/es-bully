package cn.pmj.bully.cluster.node;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.exception.BullyElectException;
import cn.pmj.bully.transport.discovery.DiscoveryChannel;
import cn.pmj.bully.transport.netty.DiscoveryServer;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.invoke.ResponseType;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

@Data
@Slf4j
public class Node {

    private Configuration configuration;
    private NodeInfo localNodeInfo;
    private List<NodeInfo> activeNodes = new CopyOnWriteArrayList<>();
    private Map<String, DiscoveryChannel> channelMap = new ConcurrentHashMap<>();
    private Map<String, NodeInfo> activeNodeInfo = new ConcurrentHashMap<>();
    private final static int RETRY_TIMES = 3;
    private ExecutorService startUp = Executors.newFixedThreadPool(2);

    private BlockingQueue<NodeInfo> joinedNodeInfo = new LinkedBlockingQueue<>();

    public Node(NodeInfo nodeInfo, Configuration configuration) {
        localNodeInfo = nodeInfo;
        this.configuration = configuration;
    }

    private List<INode> nodesShortInfo() {
        List<INode> nodes = new ArrayList<>();
        List<String> hosts = configuration.getHosts();
        for (String host : hosts) {
            String[] split = host.split(":");
            nodes.add(new ShortNodeInfo(split[0], Integer.valueOf(split[1])));
        }
        return nodes;
    }

    public void put(DiscoveryChannel discoveryChannel) {
        channelMap.putIfAbsent(discoveryChannel.getDest().getNodeId(), discoveryChannel);
        activeNodeInfo.putIfAbsent(discoveryChannel.getDest().getNodeId(), discoveryChannel.getDest());
        localNodeInfo.getClusterState().addActiveNodeInfo(discoveryChannel.getDest().getNodeId(), discoveryChannel.getDest());
    }

    /**
     * @param nodeId
     */
    public void remove(String nodeId) {
        DiscoveryChannel discoveryChannel = channelMap.get(nodeId);
        NodeInfo nodeInfo = discoveryChannel.getDest();
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

    public void doStart() throws Exception {
        Thread.sleep(1000);
        connectToOtherNode();
        startElect();

    }

    private void startElect() {
        while (!enoughNodeNumCheck()) {
            log.warn("node num at least need :{},but found:{}", configuration.getQuorum(), channelMap.size());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        NodeInfo master = findMaster();
        if (master == null) {
            startElect();
        }
        if (master != null) {
            if (master.equals(localNodeInfo)) {
                pendingElectedMaster(new ElectCallBack() {
                    @Override
                    public void success() {
                        ClusterState clusterState = localNodeInfo.getClusterState();
                        clusterState.setMaster(localNodeInfo);
                        //发布状态,开始监听其他从节点
                    }

                    @Override
                    public void failed(Exception e) {
                        startElect();
                    }
                });
            } else {
                joinElectedMaster(master, new ElectCallBack() {
                    @Override
                    public void success() {
                        //监听主节点
                    }

                    @Override
                    public void failed(Exception e) {
                        startElect();
                    }
                });
            }
        }
    }


    public void connectToOtherNode() {
        List<INode> nodeList = nodesShortInfo();


        startUp.execute(() -> {
            for (; ; ) {
                if (channelMap.size() == configuration.getHosts().size()) {
                    break;
                }
                for (INode node : nodeList) {
                    boolean exist = channelMap.values().stream().anyMatch((channel) -> {
                        String host = channel.getDest().getHost();
                        Integer port = channel.getDest().getPort();
                        return host.equals(node.getHost()) && port.equals(node.getPort());
                    });
                    if (exist) {
                        continue;
                    }
                    try {
                        TcpClient.connect(localNodeInfo, node, this, (future) -> {
                            if (future.isSuccess()) {
                                log.info("连接至：{}:{}", node.getHost(), node.getPort());
                            }
                        });
                    }catch (Exception e){
                        log.info("Exception:{}", e.getMessage());
                    }

                }
            }
        });
    }


    public void bind(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>> listener) {
        DiscoveryServer server = new DiscoveryServer(getLocalNodeInfo(), listener);
        startUp.execute(server);
    }


    private boolean enoughNodeNumCheck() {
        return channelMap.size() >= configuration.getQuorum();
    }

    private void joinElectedMaster(NodeInfo master, ElectCallBack electCallBack) {
        DiscoveryChannel discoveryChannel = channelMap.get(master.getNodeId());
        if (discoveryChannel == null) {
            electCallBack.failed(new BullyElectException("channel为空"));
            return;
        }
        discoveryChannel.joinCluster();
    }

    private void pendingElectedMaster(ElectCallBack callBack) {
        int count = 0;
        Long timeOut = System.currentTimeMillis() + 3600;
        while (count < configuration.getQuorum()) {
            if (System.currentTimeMillis() >= timeOut) {
                callBack.failed(new TimeoutException("选举超时"));
                break;
            }
            try {
                NodeInfo take = joinedNodeInfo.take();
                Long remoteVersion = take.getClusterState().getElectVersion();
                Long currentVersion = localNodeInfo.getClusterState().getElectVersion();
                if (remoteVersion < currentVersion) {
                    continue;
                } else if (remoteVersion == currentVersion) {
                    count++;
                } else {
                    callBack.failed(new BullyElectException("elect failed"));
                }
            } catch (InterruptedException e) {
                callBack.failed(e);
            }
        }
        if (count >= configuration.getQuorum()) {
            callBack.success();
        }
    }

    private NodeInfo findMaster() {
        List<Future<BullyResponse>> futures = new ArrayList<>();
        for (DiscoveryChannel value : channelMap.values()) {
            futures.add(value.elect());
        }
        List<NodeInfo> activeNodes = new ArrayList<>();
        List<BullyResponse> result = new ArrayList<>();
        List<NodeInfo> masters = new ArrayList<>();
        futures.forEach((responseFuture) -> {
            try {
                BullyResponse response = responseFuture.get();
                if (response.getMsgType() == ResponseType.PONG) {
                    NodeInfo nodeInfo = response.getNodeInfo();
                    result.add(response);
                    activeNodes.add(nodeInfo);
                    masters.add(nodeInfo.getClusterState().getMaster());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        if (result.size() < configuration.getQuorum()) {
            log.warn("node num at least need :{},but found:{}", configuration.getQuorum(), result.size());
            return null;
        }
        if (masters.size() != 0) {
            //获取当前集群中已经存在的master节点
            return extractMaster(masters);
        }
        if (activeNodes.size() != 0) {
            return getCandiateMater(activeNodes);
        }

        return null;
    }


    private NodeInfo getCandiateMater(List<NodeInfo> nodeInfos) {
        NodeInfo nodeInfo = extractMaster(nodeInfos);
        if (nodeInfo == null) {
            return null;
        }
        int count = 0;
        for (NodeInfo info : nodeInfos) {
            if (info.getNodeId().equals(info.getNodeId())) {
                count++;
            }
        }
        if (count >= configuration.getQuorum()) {
            return nodeInfo;
        }
        return null;
    }

    private NodeInfo extractMaster(List<NodeInfo> nodeInfos) {
        if (nodeInfos == null || nodeInfos.size() == 0) {
            return null;
        }
        Optional<NodeInfo> nodeInfo = nodeInfos.stream().min((o1, o2) -> {
            if (o1.getId() > o2.getId()) {
                return -1;
            }
            return 1;
        });
        return nodeInfo.get();
    }


    interface ElectCallBack {
        void success();

        void failed(Exception e);
    }
}
