package cn.pmj.bully.transport.netty.invoke;

import java.util.concurrent.TimeUnit;

public interface Result {




     void get();



     void get(Integer time, TimeUnit timeUnit);
}
