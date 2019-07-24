package rpc;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.DiscoveryServer;
import cn.pmj.bully.transport.netty.BullyRequest;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.ping.DiscoveryChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class RpcTest {

    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void test1() throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        NodeInfo nodeInfo = new NodeInfo(null);
        nodeInfo.setHost("localhost");
        nodeInfo.setPort(9000);
        DiscoveryServer server = new DiscoveryServer(nodeInfo,(future) -> {
            if (future.isSuccess()) {
                log.info("success");
            }
        });
        executorService.execute(server);

        Thread.sleep(4);
        DiscoveryChannel connect1 = TcpClient.connect(nodeInfo);
        BullyRequest bullyRequest = new BullyRequest();
        bullyRequest.setBody("------");
        bullyRequest.setRequestId("123123123");
        DiscoveryChannel.InvokeFuture future = connect1.writeAndFlush(bullyRequest);


        BullyRequest request = new BullyRequest();
        request.setRequestId("request");
        request.setBody("txt");
        DiscoveryChannel.InvokeFuture invokeFuture = connect1.writeAndFlush(request);
        Thread.sleep(10000);
    }

}
