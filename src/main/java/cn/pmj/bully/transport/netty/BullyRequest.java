package cn.pmj.bully.transport.netty;

import lombok.Data;

@Data
public class BullyRequest {

    private String requestId;

    private String body;
}
