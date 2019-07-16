package cn.pmj.bully.transport.ping;

public class UnicastMessage {


    /**
     * 请求的id
     */
    private String id;


    /**
     * 选举的版本
     */
    private String version;

    /**
     * 哪一个节点过来的
     */
    private String from;

    /**
     * 选举谁
     */
    private String candicate;


    /**
     * 消息类型
     */
    private UniCastType type;
}
