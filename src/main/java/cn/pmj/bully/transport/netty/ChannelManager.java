package cn.pmj.bully.transport.netty;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class ChannelManager {


    private static ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>(16);


    public static Channel get(String nodeId) {
        return channels.get(nodeId);
    }

    public static void put(String nodeId, Channel channel) {
        channels.put(nodeId, channel);
    }

    public static void evict(String nodeId) {
        channels.remove(nodeId);
    }
}
