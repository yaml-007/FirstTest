package org.yaml.lee.component;

import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class MyHealthCheckHandler implements HealthCheckHandler {
    @Autowired
    private MyHealthIndicator healthIndicator;

    @Override
    public InstanceInfo.InstanceStatus getStatus(InstanceInfo.InstanceStatus currentStatus) {
        Status state = healthIndicator.getHealthState();
        if (state.equals(Status.UP)){
            return InstanceInfo.InstanceStatus.UP;
        }else {
            return InstanceInfo.InstanceStatus.DOWN;
        }
    }
}
