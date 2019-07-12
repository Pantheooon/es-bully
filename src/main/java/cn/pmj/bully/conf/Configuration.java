package cn.pmj.bully.conf;

import lombok.Data;

import java.util.List;

@Data
public class Configuration {


    private String clusterId;

    private String host;

    private Integer port;

    private Integer electTimes;

    private List<String> hosts;

    private Integer quorum;


    public static Configuration getConfiguration(String confPath) {
        return new Configuration();
    }


}
