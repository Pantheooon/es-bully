package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.INode;
import cn.pmj.bully.cluster.node.Node;
import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.invoke.BullyRequest;
import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.invoke.RequestType;
import cn.pmj.bully.transport.netty.serialize.BullyDecoder;
import cn.pmj.bully.transport.netty.serialize.BullyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class TcpClient {


    public static void connect(NodeInfo localNodeInfo, INode destNodeInfo, Node node, GenericFutureListener<? extends Future<? super Void>> listener) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new BullyDecoder(BullyResponse.class));
                        ch.pipeline().addLast(new BullyEncoder());
                        ch.pipeline().addLast(new ClientHandler(localNodeInfo, node));

                    }
                });
        bootstrap.connect(new InetSocketAddress(destNodeInfo.getHost(), destNodeInfo.getPort())).sync().addListener(listener);
    }


}
