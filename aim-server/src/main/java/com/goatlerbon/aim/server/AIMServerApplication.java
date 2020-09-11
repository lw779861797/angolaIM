package com.goatlerbon.aim.server;

import com.goatlerbon.aim.server.config.AppConfiguration;
import com.goatlerbon.aim.server.kit.RegistryZK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

/**
 * CommandLineRunner用于SpringBoot启动时执行任务
 */
@SpringBootApplication
public class AIMServerApplication implements CommandLineRunner {

    @Autowired
    private AppConfiguration appConfiguration;

    @Value("${server.port}")
    private int port;

    private final static Logger LOGGER = LoggerFactory.getLogger(AIMServerApplication.class);

    @Override
    public void run(String... args) throws Exception {
//        获得本机的IP
        String addr = InetAddress.getLocalHost().getHostAddress();
        Thread thread = new Thread(new RegistryZK(addr,appConfiguration.getAimServerPort(),port));
        thread.setName("registry-zk");
        thread.start();
    }

    public static void main(String[] args) {
        SpringApplication.run(AIMServerApplication.class,args);
        LOGGER.info("Start cim server success!!!");
    }
}
