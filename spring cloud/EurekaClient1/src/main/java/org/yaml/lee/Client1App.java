package org.yaml.lee;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.Scanner;

@SpringBootApplication
@EnableEurekaClient
public class Client1App {
    public static void main(String[] args) {
        System.out.println("Please set the server port");
        Scanner sc = new Scanner(System.in);
        String port = sc.nextLine();
        new SpringApplicationBuilder(Client1App.class).properties("server.port=" + port).run(args);
    }
}
