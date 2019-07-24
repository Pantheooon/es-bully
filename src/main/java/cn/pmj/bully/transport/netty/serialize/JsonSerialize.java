package cn.pmj.bully.transport.netty.serialize;

import cn.pmj.bully.transport.netty.serialize.core.ISerialize;
import com.alibaba.fastjson.JSON;

public class JsonSerialize implements ISerialize {



    @Override
    public <T> byte[] encode(T o) {
        return JSON.toJSONString(o).getBytes();
    }

    @Override
    public <T> T decode(byte[] bytes, Class<T> tClass) {
        return JSON.parseObject(new String(bytes), tClass);
    }
}
