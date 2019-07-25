package cn.pmj.bully.transport.netty;

import cn.pmj.bully.transport.netty.invoke.BullyResponse;
import cn.pmj.bully.transport.netty.invoke.ResponseHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

   private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        executorService.scheduleAtFixedRate(()->{
            log.info("currentTime:{}",System.currentTimeMillis());
        },0,1, TimeUnit.SECONDS);
        log.info("启动成功。。。。。。。");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BullyResponse bullyResponse = (BullyResponse)msg;
        ResponseHolder.setResponse(bullyResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
