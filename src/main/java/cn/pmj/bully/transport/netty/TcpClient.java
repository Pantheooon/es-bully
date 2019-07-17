package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.ping.DiscoveryPing;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpClient {

    private static final int MAX_RETRY = 5;


    private static final int MAX_AWAIT = 5;

    private EventLoopGroup group = new NioEventLoopGroup(1);

    private Bootstrap bootstrap = new Bootstrap();

    private NodeInfo nodeInfo;

    private Channel channel;


    private CountDownLatch downLatch = new CountDownLatch(1);

    public TcpClient(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public DiscoveryPing connect(Integer retry) throws InterruptedException {
        log.info("id:{},host:{},port:{},starting-----", nodeInfo.getNodeId(), nodeInfo.getHost(), nodeInfo.getPort());
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast();
                    }
                });
        ChannelFuture sync = bootstrap.connect(new InetSocketAddress(nodeInfo.getHost(), nodeInfo.getPort())).sync().
                addListener((listner) -> {
                    if (!listner.isSuccess()) {
                        if (retry == 0) {
                            log.info("connect to node:{} failed",nodeInfo.getNodeId());
                        } else {
                            if (channel == null){
                                log.info("failed connect:id:{},host:{},port:{},times:{},reconenct-----", nodeInfo.getNodeId(), nodeInfo.getHost(), nodeInfo.getPort(), retry);
                                int order = MAX_RETRY - retry;
                                int delay = 1 << order;
                                TimeUnit.SECONDS.sleep(delay);
                                Integer next = retry - 1;
                                connect(next);
                            }

                        }
                    }else {
                        downLatch.countDown();
                    }
                });
        channel = sync.channel();
        return  getDiscoverCoveryPing();
    }


    public void shutDownGracefully() {
        group.shutdownGracefully();
    }


    public DiscoveryPing getDiscoverCoveryPing(){
        try {
            downLatch.await(MAX_AWAIT,TimeUnit.SECONDS);
            return new DiscoveryPing();
        } catch (InterruptedException e) {
            log.info("error");
        }
        return null;
    }
}
