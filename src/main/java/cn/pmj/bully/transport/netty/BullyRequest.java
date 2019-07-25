package cn.pmj.bully.transport.netty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BullyRequest {

    private String nodeId;

    private String requestId;

    private String body;

    private Integer msgType;

    private Long version;

    private Long timeOut;
}
