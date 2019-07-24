package cn.pmj.bully.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("msg-->{}", msg);
        BullyRequest request =(BullyRequest)msg;
        BullyResponse response = new BullyResponse();
        response.setRequestId(request.getRequestId());
        ctx.writeAndFlush(response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
