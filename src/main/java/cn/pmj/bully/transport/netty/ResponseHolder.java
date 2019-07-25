package cn.pmj.bully.transport.netty;

import cn.pmj.bully.transport.netty.invoke.InvokeFuture;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResponseHolder {


    private static Map<String, InvokeFuture> map = new HashMap<>();

    static {

        clearTimeOutMessage();
    }


    private static void clearTimeOutMessage(){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(()->{
            //todo


        },0,1, TimeUnit.SECONDS);
    }

    public static void setResponse(BullyResponse response) {
        InvokeFuture invokeFuture = map.get(response.getRequestId());
        if (invokeFuture != null) {
            CountDownLatch latch = invokeFuture.getLatch();
            invokeFuture.setResponse(response);
            long count = latch.getCount();
            if (count == 1) {
                latch.countDown();
            }
        }
    }


    public static void setFuture(InvokeFuture future) {
        BullyRequest request = future.getRequest();
        map.put(request.getRequestId(), future);
    }


}
