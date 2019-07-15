package cn.pmj.bully.transport.unicast;

public enum UniCastType {

    /**
     * normal elect
     */
    ELECT,

    /**
     * re-join cluster after node crashed
     */
    REJOIN,

    /**
     * ping
     */
    PING,

    /**
     * pong
     */
    PONG,

    /**
     * master has checked out the cn.pmj.bully.cluster.node crashed
     */
    CRASHED;
}
