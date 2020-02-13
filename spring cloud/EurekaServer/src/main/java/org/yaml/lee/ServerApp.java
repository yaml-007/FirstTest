package org.yaml.lee;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServerApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServerApp.class).run(args);
    }
}
