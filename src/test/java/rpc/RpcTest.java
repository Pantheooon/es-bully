package rpc;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.DiscoveryServer;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.TcpClient;
import cn.pmj.bully.transport.netty.invoke.InvokeFuture;
import cn.pmj.bully.transport.discovery.DiscoveryChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j
public class RpcTest {

    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void test1() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        NodeInfo nodeInfo = new NodeInfo(null);
        nodeInfo.setHost("localhost");
        nodeInfo.setPort(9000);
        DiscoveryServer server = new DiscoveryServer(nodeInfo, (future) -> {
            if (future.isSuccess()) {
                log.info("success");
            }
        });
        executorService.execute(server);

        Thread.sleep(4);
        DiscoveryChannel connect1 = TcpClient.connect(null, nodeInfo);
        BullyRequest bullyRequest = new BullyRequest();
        bullyRequest.setBody("------");
        bullyRequest.setRequestId("123123123");
        InvokeFuture future = connect1.writeAndFlush(bullyRequest);
        BullyResponse bullyResponse = future.get();
        log.info("--測試--->1{},{}", bullyRequest, bullyResponse);

        BullyRequest request = new BullyRequest();
        request.setRequestId("request");
        request.setBody("txt");
        InvokeFuture invokeFuture = connect1.writeAndFlush(request);
        BullyResponse response = invokeFuture.get();
        log.info("---測試-->2{},{}", request, response);
        Thread.sleep(10000);
    }

}
