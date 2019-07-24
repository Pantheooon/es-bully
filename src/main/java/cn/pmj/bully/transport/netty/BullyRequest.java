package cn.pmj.bully.transport.netty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BullyRequest {

    private String requestId;

    private String body;
}
