package cn.pmj.bully;

import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.transport.netty.BullyResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DiscoveryMain {


    private static String confPath = "";

    private Configuration configuration = Configuration.getConfiguration(confPath);

    private NodeInfo localNodeInfo;

    private Node node;

    public static void main(String[] args) throws Exception {
        DiscoveryMain discoveryMain = new DiscoveryMain();
        discoveryMain.start();
    }

    private void start() {
        localNodeInfo = localNode();
        node = new Node(localNodeInfo, configuration);
        node.bind();
        NodeInfo master = findMaster();
        if (master.equals(localNodeInfo)) {
            pendingElectedMaster();
        } else {
            joinElectedMaster();
        }
    }

    private void joinElectedMaster() {
    }

    private void pendingElectedMaster() {
    }

    private NodeInfo findMaster() {
        List<BullyResponse> ping = node.ping();

        return null;
    }

    private NodeInfo localNode() {

        return new NodeInfo(configuration);
    }

    @FunctionalInterface
    interface CallBack {

        void callBack(ClassBackEnvent envent);
    }

    class ClassBackEnvent {

    }
}
