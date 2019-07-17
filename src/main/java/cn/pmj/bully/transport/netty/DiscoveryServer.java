package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.NodeInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DiscoveryServer {


    private NodeInfo nodeInfo;
    private ServerBootstrap bootstrap = new ServerBootstrap();
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    public DiscoveryServer(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }


    public void doBind(GenericFutureListener<? extends Future<? super Void>> listener) throws InterruptedException {

        try {
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ServerChannelInitializer());
            ChannelFuture sync = bootstrap.bind(nodeInfo.getHost(), nodeInfo.getPort()).sync();
            ChannelFuture future = sync.channel().closeFuture().sync();
            future.addListener(listener);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    private static class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ObjectDecoder(65536 * 1024, ClassResolvers.cacheDisabled(null)));
            ch.pipeline().addLast(new ObjectEncoder());
        }
    }
}
