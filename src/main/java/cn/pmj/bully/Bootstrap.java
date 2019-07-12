package cn.pmj.bully;

import cn.pmj.bully.conf.Configuration;
import cn.pmj.bully.node.Node;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bootstrap {


    private static String confPath = "";

    public static void main(String[] args) throws Exception {
        log.info("start--->");
        Configuration configuration = Configuration.getConfiguration(confPath);
        Node node = new Node(configuration);
        node.start();
    }
}
