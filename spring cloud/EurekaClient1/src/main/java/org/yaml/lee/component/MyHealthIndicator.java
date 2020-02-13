package org.yaml.lee.component;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class MyHealthIndicator implements HealthIndicator {
    private static volatile Status healthState = Status.UP;

    public Status getHealthState() {
        return healthState;
    }

    public void setHealthState(Status healthState) {
        MyHealthIndicator.healthState = healthState;
    }

    @Override
    public Health health() {
        return Health.status(healthState).build();
    }
}
