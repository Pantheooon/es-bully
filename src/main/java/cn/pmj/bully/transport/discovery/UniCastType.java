package cn.pmj.bully.transport.discovery;

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
     * discovery
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
