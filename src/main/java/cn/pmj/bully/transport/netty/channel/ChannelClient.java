package cn.pmj.bully.transport.netty.channel;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.ChannelManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.rmi.AccessException;

@Slf4j
public class ChannelClient {

    final static EventLoopGroup group = new NioEventLoopGroup(1);

    public static Channel connect(NodeInfo nodeInfo, Integer retry) throws InterruptedException {

        log.info("id:{},host:{},port:{},starting-----", nodeInfo.getNodeId(), nodeInfo.getHost(), nodeInfo.getPort());
        Channel channel = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast();
                        }
                    });
            ChannelFuture sync = b.connect(new InetSocketAddress(nodeInfo.getHost(), nodeInfo.getPort())).sync().
                    addListener((listner) -> {
                        if (!listner.isSuccess()) {
                            if (retry == 0) {
                                throw new AccessException("");
                            }else {
                                log.info("failed connect:id:{},host:{},port:{},times:{},reconenct-----", nodeInfo.getNodeId(), nodeInfo.getHost(), nodeInfo.getPort(), retry);
                                Integer next = retry - 1;
                                connect(nodeInfo, next);
                            }
                        }
                    });
            channel = sync.channel();
            ChannelManager.put(nodeInfo.getNodeId(), channel);
        } finally {
            group.shutdownGracefully();
        }
        return channel;
    }

}
