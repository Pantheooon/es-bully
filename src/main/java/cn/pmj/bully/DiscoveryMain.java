package cn.pmj.bully;

import cn.pmj.bully.cluster.ClusterState;
import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.conf.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class DiscoveryMain {


    private static String confPath = "bully.properties";

    private static Configuration configuration;

    private NodeInfo localNodeInfo;

    private Node node;


    public static void main(String[] args) throws Exception {

        DiscoveryMain discoveryMain = new DiscoveryMain();
        discoveryMain.start();

    }


    private void setUp() throws IOException {
        configuration = Configuration.getConfiguration(confPath);
        localNodeInfo = localNode();
        node = new Node(localNodeInfo, configuration);
    }


    private void start() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        setUp();
        node.bind((future) -> {
            if (future.isSuccess()) {
                log.info("node:{},bind port:{}," +
                        "successfully", localNodeInfo.getNodeId(), localNodeInfo.getPort());
                latch.countDown();

            } else {
                log.info("node server started failed ,cause:{}", future.cause().getLocalizedMessage());
            }
        });
        latch.await();
        node.doStart();
    }





    private NodeInfo localNode() {

        return new NodeInfo(configuration);
    }

}
