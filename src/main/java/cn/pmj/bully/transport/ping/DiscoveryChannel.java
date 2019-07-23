package cn.pmj.bully.transport.ping;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.BullyRequest;
import cn.pmj.bully.transport.netty.BullyResponse;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class DiscoveryChannel {


    private NodeInfo nodeInfo;


    private Channel channel;

    private InvokeFuture InvokeFuture;

    public DiscoveryChannel(Channel channel) {
        this.channel = channel;
    }

    public Future<BullyResponse> ping() {
        InvokeFuture.invoke();
        return null;
    }


    public InvokeFuture writeAndFlush(BullyRequest bullyRequest) {
        channel.writeAndFlush(bullyRequest);
        return new InvokeFuture(bullyRequest);
    }


    public class InvokeFuture implements Future<BullyResponse> {

        private CountDownLatch latch = new CountDownLatch(1);

        private BullyResponse response;

        private BullyRequest request;

        public InvokeFuture(BullyRequest bullyRequest) {
            this.request = bullyRequest;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }


        public void invoke() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return response == null;
        }

        @Override
        public BullyResponse get() throws InterruptedException, ExecutionException {
            try {
                return get(2, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public BullyResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            latch.await(timeout, unit);
            return response;
        }
    }


}
