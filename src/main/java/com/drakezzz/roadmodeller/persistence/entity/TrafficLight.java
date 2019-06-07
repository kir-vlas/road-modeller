package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class TrafficLight {

    private static final double YELLOW_DELAY = 60;

    private LightStatus status;

    private LightStatus previousStatus;

    private LightStatus visibleStatus;

    private LightStatus previousVisibleStatus;

    private double redDelay;

    private double greenDelay;

    private double currentDuration;

    private Point coordinates;

    private void incrementCurrentDuration() {
        currentDuration++;
    }

    public void changeLight() {
        incrementCurrentDuration();
        changeVisibleStatus();
        if (status.equals(LightStatus.GREEN)) {
            if (currentDuration < greenDelay) {
                return;
            }
            previousStatus = LightStatus.GREEN;
            status = LightStatus.YELLOW;
            currentDuration = 0;
            return;
        }
        if (status.equals(LightStatus.YELLOW)) {
            if (currentDuration < YELLOW_DELAY) {
                return;
            }
            if (previousStatus.equals(LightStatus.GREEN)) {
                status = LightStatus.RED;
            } else if (previousStatus.equals(LightStatus.RED)) {
                status = LightStatus.GREEN;
            }
            currentDuration = 0;
            return;
        }
        if (status.equals(LightStatus.RED)) {
            if (currentDuration < redDelay) {
                return;
            }
            previousStatus = LightStatus.RED;
            status = LightStatus.YELLOW;
            currentDuration = 0;
        }
    }

    private void changeVisibleStatus() {
        if (visibleStatus.equals(LightStatus.GREEN)) {
            if (currentDuration < greenDelay) {
                return;
            }
            previousVisibleStatus = LightStatus.GREEN;
            visibleStatus = LightStatus.YELLOW;
            return;
        }
        if (visibleStatus.equals(LightStatus.YELLOW)) {
            if (currentDuration < YELLOW_DELAY) {
                return;
            }
            if (previousVisibleStatus.equals(LightStatus.GREEN)) {
                visibleStatus = LightStatus.RED;
            } else if (previousVisibleStatus.equals(LightStatus.RED)) {
                visibleStatus = LightStatus.GREEN;
            }
            return;
        }
        if (visibleStatus.equals(LightStatus.RED)) {
            if (currentDuration < redDelay) {
                return;
            }
            previousVisibleStatus = LightStatus.RED;
            visibleStatus = LightStatus.YELLOW;
        }
    }

}
