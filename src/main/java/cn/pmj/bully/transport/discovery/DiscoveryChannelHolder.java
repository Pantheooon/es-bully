package cn.pmj.bully.transport.discovery;

import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryChannelHolder {


    private Map<String,DiscoveryChannel> channelMap = new ConcurrentHashMap<>();

    private Node node;


    public void put(DiscoveryChannel discoveryChannel){
        channelMap.putIfAbsent(discoveryChannel.getNodeInfo().getNodeId(),discoveryChannel);
    }

    /**
     *
     * @param nodeId
     */
    public void remove(String nodeId){
        DiscoveryChannel discoveryChannel = channelMap.get(nodeId);
        NodeInfo nodeInfo = discoveryChannel.getNodeInfo();
        if ( nodeInfo.getRole()== NodeInfo.Role.MASTER && checkMasterNodeOffLine()){}

    }



    public boolean checkMasterNodeOffLine(){
        return false;
    }

    public void removeSlaveNodeThenBroadCast(){}

}
