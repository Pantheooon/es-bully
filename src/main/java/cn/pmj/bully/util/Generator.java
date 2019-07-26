package cn.pmj.bully.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator {


    private  static AtomicInteger count = new AtomicInteger(0);

    /**
     * @return
     */
    public static Integer version() {
        return count.incrementAndGet();
    }

    public static Integer version(Integer version,boolean incrementGet){
        count.set(version);
        if (incrementGet){
            return count.incrementAndGet();
        }
        return version;
    }


    /**
     * @return
     */
    public static String requestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成nodeId.采用bully算法中用pid
     * @return
     */
    public static Integer nodeId(){
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        return Integer.parseInt(name.substring(0, name.indexOf('@')));
    }
}
