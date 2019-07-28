package cn.pmj.bully.cluster.node;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.exception.BullyElectException;
import cn.pmj.bully.transport.discovery.DiscoveryChannel;
import cn.pmj.bully.transport.netty.DiscoveryServer;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
    private ExecutorService startUp = Executors.newFixedThreadPool(2);

    private BlockingQueue<NodeInfo> joinedNodeInfo = new LinkedBlockingQueue<>();

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

    public void doStart() throws Exception {
        connectToOtherNode();
        startElect();

    }

    private void startElect(){
        while (!enoughNodeNumCheck()) {
            log.warn("node num at least need :{},but found:{}", configuration.getQuorum(), channelMap.size());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        NodeInfo master = findMaster();
        if (master == null){
            startElect();
        }
        if (master != null){
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
        startUp.execute(() -> {
            for (NodeInfo nodeInfo : nodeList) {
                DiscoveryChannel channel = null;
                try {
                    while ((channel = TcpClient.connect(localNodeInfo, nodeInfo)) != null) {
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


    public void bind(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>> listener) {
        DiscoveryServer server = new DiscoveryServer(getLocalNodeInfo(), listener);
        startUp.execute(server);
    }


    private boolean enoughNodeNumCheck() {
        return channelMap.size() >= configuration.getQuorum();
    }

    private void joinElectedMaster(NodeInfo master,ElectCallBack electCallBack) {
        DiscoveryChannel discoveryChannel = channelMap.get(master.getNodeId());
        if (discoveryChannel == null){
            electCallBack.failed(new BullyElectException("channel为空"));
            return;
        }
        discoveryChannel.joinCluster();
    }

    private void pendingElectedMaster(ElectCallBack callBack) {
        int count = 0;
        Long timeOut = System.currentTimeMillis()+3600;
        while (count<configuration.getQuorum()){
            if (System.currentTimeMillis()>=timeOut){
                callBack.failed(new TimeoutException("选举超时"));
                break;
            }
            try {
                NodeInfo take = joinedNodeInfo.take();
                Long remoteVersion = take.getClusterState().getElectVersion();
                Long currentVersion = localNodeInfo.getClusterState().getElectVersion();
                if (remoteVersion<currentVersion){
                    continue;
                }else if (remoteVersion == currentVersion){
                    count++;
                }else {
                    callBack.failed(new BullyElectException("elect failed"));
                }
            } catch (InterruptedException e) {
                callBack.failed(e);
            }
        }
        if (count>=configuration.getQuorum()){
            callBack.success();
        }
    }

    private NodeInfo findMaster() {
        List<Future<BullyResponse>> futures = new ArrayList<>();
        for (DiscoveryChannel value : channelMap.values()) {
            futures.add(value.elect());
        }
        List<NodeInfo> activeNode = new ArrayList<>();
        List<BullyResponse> result = new ArrayList<>();
        List<NodeInfo> masters = new ArrayList<>();
        futures.forEach((responseFuture) -> {
            try {
                BullyResponse response = responseFuture.get();
                NodeInfo nodeInfo = response.getNodeInfo();
                result.add(response);
                activeNode.add(nodeInfo);
                masters.add(nodeInfo.getClusterState().getMaster());
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
        if (masters.size() != 0){
            //获取当前集群中已经存在的master节点
           return extractMaster(masters);
        }
        if (activeNode.size() != 0){
            return getCandiateMater(activeNode);
        }

        return null;
    }



    private NodeInfo getCandiateMater(List<NodeInfo> nodeInfos){
        NodeInfo nodeInfo = extractMaster(nodeInfos);
        if (nodeInfo == null){
            return null;
        }
        int count = 0;
        for (NodeInfo info : nodeInfos) {
            if (info.getNodeId().equals(info.getNodeId())){
                count ++;
            }
        }
        if (count>=configuration.getQuorum()){
            return nodeInfo;
        }
        return null;
    }

    private NodeInfo extractMaster(List<NodeInfo> nodeInfos){
        if (nodeInfos == null || nodeInfos.size() == 0){
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


    interface ElectCallBack{
        void success();
        void failed(Exception e);
    }
}
