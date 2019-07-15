package cn.pmj.bully.cluster.node;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.transport.netty.channel.ChannelClient;
import cn.pmj.bully.transport.netty.channel.ChannelServer;
import io.netty.channel.Channel;
import cn.pmj.bully.transport.unicast.UniCast;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class Node {


    private List<NodeInfo> nodeList;
    private Map<String, UniCast> uniCastMap;
    private Configuration configuration;
    private NodeInfo master;
    private NodeInfo localNode;
    private List<NodeInfo> activeNodes = new ArrayList<>();

    public Node(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() throws Exception {
        //1.bind local address
        bind();
        //2.connect 2 other nodes
        connect();
        //3.elect
        master = electMaster();
        if (master == null) {
            //after n loop elections ,still no master,throw a exception
        }
        //if the cn.pmj.bully.cluster.node is master,receive the meassage from slave to ping and pong ,if slave,

    }

    private NodeInfo electMaster() {
        NodeInfo node = null;
        Integer loopTimes = configuration.getElectTimes();
        for (Integer i = 0; i < loopTimes; i++) {
            node = findMaster();
            if (node != null) {
                break;
            }
        }
        return node;
    }

    private void connect() {
        List<NodeInfo> nodeList = getNodeList();
        for (NodeInfo nodeInfo : nodeList) {
            if (!localNode.equals(nodeInfo)) {
                try {
                    Channel channel = ChannelClient.connect(nodeInfo, 3);
                    if (channel != null) {
                        activeNodes.add(nodeInfo);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void bind() {
        try {
            ChannelServer.connect(localNode);
        } catch (InterruptedException e) {
            log.error("start failed-->{}", e.getLocalizedMessage());
        }
    }


    private NodeInfo findMaster() {
        return null;
    }

}
