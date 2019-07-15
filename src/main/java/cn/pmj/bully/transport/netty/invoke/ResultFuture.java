package cn.pmj.bully.transport.netty.invoke;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResultFuture {


    private final static Integer DEFAULT_TIME_OUT = 2000;

    private final CountDownLatch LATCH = new CountDownLatch(1);

    public void get(Integer time, TimeUnit timeUnit){}

}
