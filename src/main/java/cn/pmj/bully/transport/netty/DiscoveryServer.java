package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.serialize.BullyDecoder;
import cn.pmj.bully.transport.netty.serialize.BullyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DiscoveryServer implements Runnable {


    private NodeInfo nodeInfo;
    private ServerBootstrap bootstrap = new ServerBootstrap();
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private EventLoopGroup bossGroup = new NioEventLoopGroup(2);

    private GenericFutureListener<? extends Future<? super Void>> listener;

    public DiscoveryServer(NodeInfo nodeInfo, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.nodeInfo = nodeInfo;
        this.listener = listener;
    }


    public void doBind() throws InterruptedException {
        try {
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new BullyDecoder(BullyRequest.class));
                            ch.pipeline().addLast(new BullyEncoder());
                            ch.pipeline().addLast(new ServerHandler(nodeInfo));
                        }
                    });
            ChannelFuture future = bootstrap.bind(nodeInfo.getHost(), nodeInfo.getPort()).addListener(listener).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        try {
            doBind();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
