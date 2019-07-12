package cn.pmj.bully.unicast.serialize;

public interface ISerialize {



    byte[] encode(Object o);


    Object decode(byte[] bytes);

}
