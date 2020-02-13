package org.yaml.lee;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Customer1App {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Customer1App.class).run(args);
    }
}
