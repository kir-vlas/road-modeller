package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class TrafficLight {

    private LightStatus status;

    private double redDelay;

    private double greenDelay;

    private double currentDuration;

    private Point coordinates;

    public void incrementCurrentDuration() {
        currentDuration++;
    }

    public void changeLight() {
        incrementCurrentDuration();
        if (status.equals(LightStatus.GREEN)) {
            if (currentDuration < greenDelay) {
                return;
            }
            status = LightStatus.RED;
            currentDuration = 0;
            return;
        }
        if (status.equals(LightStatus.RED)) {
            if (currentDuration < redDelay) {
                return;
            }
            status = LightStatus.GREEN;
            currentDuration = 0;
        }
    }

}
