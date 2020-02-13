package org.yaml.lee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.lee.component.MyHealthIndicator;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestController {

    @Autowired
    private MyHealthIndicator healthIndicator;

    @GetMapping("/setClientHealth/{state}")
    public String setClientHealth(@PathVariable String state) {
        if (state.toUpperCase().equals("UP") && healthIndicator.getHealthState().equals(Status.DOWN)) {
            synchronized (TestController.class) {
                healthIndicator.setHealthState(Status.UP);
            }
        }
        if (state.toUpperCase().equals("DOWN") && healthIndicator.getHealthState().equals(Status.UP)) {
            synchronized (TestController.class) {
                healthIndicator.setHealthState(Status.DOWN);
            }
        }
        return "forward:/actuator/health";
    }

    @RequestMapping("/test/{id}")
    public @ResponseBody String test(@PathVariable String id) {
        return id;
    }

    @RequestMapping("/getURL")
    @ResponseBody
    public String getURL(HttpServletRequest request){
        return request.getRequestURL().toString();
    }

}
