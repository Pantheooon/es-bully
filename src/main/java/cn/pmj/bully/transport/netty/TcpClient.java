package cn.pmj.bully.transport.netty;

import cn.pmj.bully.cluster.node.NodeInfo;
import cn.pmj.bully.transport.netty.serialize.BullyDecoder;
import cn.pmj.bully.transport.netty.serialize.BullyEncoder;
import cn.pmj.bully.transport.ping.DiscoveryChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class TcpClient {


    private static EventLoopGroup group = new NioEventLoopGroup(1);

    private static Bootstrap bootstrap = new Bootstrap();




    public static DiscoveryChannel connect(NodeInfo nodeInfo) throws InterruptedException {
        log.info("id:{},host:{},port:{},starting-----", nodeInfo.getNodeId(), nodeInfo.getHost(), nodeInfo.getPort());
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new BullyDecoder());
                        ch.pipeline().addLast(new BullyEncoder());
                        ch.pipeline().addLast(new ClientHandler());

                    }
                });
        ChannelFuture sync = bootstrap.connect(new InetSocketAddress(nodeInfo.getHost(), nodeInfo.getPort())).sync();
        return new DiscoveryChannel(sync.channel());
    }


}
