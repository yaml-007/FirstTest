package org.yaml.lee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@Configuration
public class TestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/findClient")
    @ResponseBody
    public String findClient(HttpServletRequest request) {
        return getRestTemplate().getForObject("http://client1/getURL", String.class);
    }

    @RequestMapping("/listServices")
    @ResponseBody
    public HashMap<String, Integer> listServices() {
        HashMap<String, Integer> services = new HashMap<>();
        List<String> servicesName = discoveryClient.getServices();
        //TODO 将服务名及数量保存到services中
        for (String serviceName : servicesName) {
            services.put(serviceName,discoveryClient.getInstances(serviceName).size());
            Optional.ofNullable(discoveryClient.getInstances(serviceName).get(0)
                                .getMetadata().get("companyName"))
                    .ifPresent(System.out::println);
        }
        return services;
    }

}
