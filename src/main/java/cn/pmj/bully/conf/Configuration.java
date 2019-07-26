package cn.pmj.bully.conf;

import lombok.Data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

@Data
public class Configuration {


    private String clusterId;

    private String host;

    private Integer port;


    private List<String> hosts;

    private Integer quorum;


    public static Configuration getConfiguration(String confPath) throws IOException {
        Properties properties = new Properties();
        String path = Configuration.class.getClassLoader().getResource(confPath).getPath();
        properties.load(new FileInputStream(path));
        String clusterId = (String) properties.get("cluster-id");
        String host = (String) properties.get("bind.host");
        Integer port = Integer.valueOf(properties.getProperty("bind.port"));
        String hosts = (String) properties.get("unicast.hosts");
        Integer quorum = Integer.valueOf((String) properties.get("elect.quorum"));
        Configuration configuration = new Configuration();
        configuration.setClusterId(clusterId);
        configuration.setHost(host);
        configuration.setPort(port);
        configuration.setHosts(Arrays.asList(hosts.split(",")));
        configuration.setQuorum(quorum);
        return configuration;
    }


}
