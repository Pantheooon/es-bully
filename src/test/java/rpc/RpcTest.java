//package rpc;
//
//import cn.pmj.bully.cluster.node.Node;
//import cn.pmj.bully.cluster.node.NodeInfo;
//import cn.pmj.bully.conf.Configuration;
//import cn.pmj.bully.transport.netty.invoke.BullyResponse;
//import cn.pmj.bully.transport.netty.DiscoveryServer;
//import cn.pmj.bully.transport.netty.invoke.BullyRequest;
//import cn.pmj.bully.transport.netty.TcpClient;
//import cn.pmj.bully.transport.netty.invoke.InvokeFuture;
//import cn.pmj.bully.transport.discovery.DiscoveryChannel;
//import cn.pmj.bully.transport.netty.invoke.ResponseType;
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.concurrent.*;
//
//@Slf4j
//public class RpcTest {
//
//    private CountDownLatch latch = new CountDownLatch(1);
//
////    @Test
////    public void test1() throws InterruptedException, ExecutionException, IOException {
////        ExecutorService executorService = Executors.newSingleThreadExecutor();
////        NodeInfo nodeInfo = new NodeInfo(Configuration.getConfiguration("bully.properties"));
////        DiscoveryServer server = new DiscoveryServer(nodeInfo, (future) -> {
////            if (future.isSuccess()) {
////                log.info("success");
////            }
////        });
////        executorService.execute(server);
////
////        Thread.sleep(4);
////        TcpClient.connect(nodeInfo, nodeInfo, null, (future) -> {
////        });
////        BullyRequest bullyRequest = new BullyRequest();
////        bullyRequest.setRequestId("123123123");
////        InvokeFuture future = connect1.writeAndFlush(bullyRequest);
////        BullyResponse bullyResponse = future.get();
////        log.info("--測試--->1{},{}", bullyRequest, bullyResponse);
////
////        BullyRequest request = new BullyRequest();
////        request.setRequestId("request");
////        InvokeFuture invokeFuture = connect1.writeAndFlush(request);
////        BullyResponse response = invokeFuture.get();
////        log.info("---測試-->2{},{}", request, response);
////        Thread.sleep(10000);
////    }
//    }
//}
