package cn.pmj.bully.transport.ping;

import cn.pmj.bully.cluster.node.NodeInfo;
import lombok.Data;

import java.nio.channels.Channel;
import java.util.concurrent.*;

public class DiscoveryPing {


    private NodeInfo nodeInfo;


    private Channel channel;

    private InvokeFuture InvokeFuture;




    public Future<DiscoveryPingResponse> ping() {
        InvokeFuture.invoke();
        return null;
    }




    class InvokeFuture implements Future{

        private CountDownLatch latch = new CountDownLatch(1);

        private DiscoveryPingResponse response;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }


        public void invoke(){}

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return response == null;
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            try {
                return get(2,TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            latch.await(timeout,unit);
            return response;
        }
    }


    @Data
    public class DiscoveryPingResponse {


        private String nodeId;

        private String masterId;

        private Long version;
    }


}
