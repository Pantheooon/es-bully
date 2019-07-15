package cn.pmj.bully.transport.netty.channel;

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

public class ChannelServer {

    public static void connect(NodeInfo nodeInfo) throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ServerChannelInitializer());
            //异步连接操作
            ChannelFuture sync = b.bind(nodeInfo.getHost(), nodeInfo.getPort()).sync();
            sync.channel().closeFuture().sync();
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
