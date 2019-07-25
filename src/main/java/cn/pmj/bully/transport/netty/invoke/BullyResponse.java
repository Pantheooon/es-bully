package cn.pmj.bully.transport.netty.invoke;

import lombok.Data;

@Data
public class BullyResponse {

    private String requestId;

    private String response;
}
