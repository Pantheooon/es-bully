package cn.pmj.bully;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.conf.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DiscoveryMain {


    private static String confPath = "bully.properties";

    private static Configuration configuration;

    private NodeInfo localNodeInfo;

    private Node node;

    private ClusterState clusterState;

    public static void main(String[] args) throws Exception {

        DiscoveryMain discoveryMain = new DiscoveryMain();
        discoveryMain.start();

    }


    private void setUp() throws IOException {
        configuration = Configuration.getConfiguration(confPath);
        localNodeInfo = localNode();
        node = new Node(localNodeInfo, configuration);
        clusterState = new ClusterState();
    }


    private void start() throws Exception {
        setUp();
        node.bind((future) -> {
            if (future.isSuccess()) {
                log.info("node:{},bind port:{}," +
                        "successfully", localNodeInfo.getNodeId(), localNodeInfo.getPort());

                node.doStart();

            } else {
                log.info("node server started failed ,cause:{}", future.cause().getLocalizedMessage());
            }
        });
    }





    private NodeInfo localNode() {

        return new NodeInfo(configuration,clusterState);
    }

}
