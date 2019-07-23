package cn.pmj.bully.transport.netty.serialize;

import cn.pmj.bully.transport.netty.BullyRequest;
import cn.pmj.bully.transport.netty.BullyResponse;
import cn.pmj.bully.transport.netty.serialize.core.ISerialize;
import com.alibaba.fastjson.JSON;

public class JsonSerialize implements ISerialize {
    @Override
    public byte[] encode(BullyRequest o) {
        return JSON.toJSONString(o).getBytes();
    }

    @Override
    public BullyResponse decode(byte[] bytes) {
        return JSON.parseObject(new String(bytes), BullyResponse.class);
    }

}
