package cn.pmj.bully.transport.netty.invoke;

import java.util.*;
import java.util.concurrent.*;

public class ResponseHolder {


    private static Map<String, InvokeFuture> map = new ConcurrentHashMap<>();

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
            if (latch.getCount() == 1) {
                latch.countDown();
            }
        }
    }


    public static void setFuture(InvokeFuture future) {
        BullyRequest request = future.getRequest();
        map.put(request.getRequestId(), future);
    }


}
