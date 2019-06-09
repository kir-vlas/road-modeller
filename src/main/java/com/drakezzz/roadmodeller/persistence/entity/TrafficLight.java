package com.drakezzz.roadmodeller.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.HashSet;
import java.util.Set;

@Data
public class TrafficLight {

    private static final double YELLOW_DELAY = 60;

    private static final double FLEX_STEP = 20;

    @JsonIgnore
    private LightStatus status;

    @JsonIgnore
    private LightStatus previousStatus;

    @JsonProperty("l")
    private LightStatus visibleStatus;

    @JsonIgnore
    private LightStatus previousVisibleStatus;

    @JsonIgnore
    private double redDelay;

    @JsonIgnore
    private double greenDelay;

    @JsonIgnore
    private double maxFlexibility;

    @JsonIgnore
    private Set<String> waitingCars = new HashSet<>();

    @JsonIgnore
    private double flexibilityFactor;

    @JsonIgnore
    private double currentDuration;

    @JsonProperty("crd")
    private Point coordinates;

    private void incrementCurrentDuration() {
        currentDuration++;
    }

    public void determineFlexibility(double waitingCars) {
        if (flexibilityFactor > maxFlexibility) {
            return;
        }
        flexibilityFactor = waitingCars * FLEX_STEP;
    }

    public void changeLight() {
        incrementCurrentDuration();
        changeVisibleStatus();
        double duration = currentDuration + flexibilityFactor;
        if (status.equals(LightStatus.GREEN)) {
            if (duration < greenDelay) {
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
            waitingCars = new HashSet<>();
            return;
        }
        if (status.equals(LightStatus.RED)) {
            if (duration < redDelay) {
                return;
            }
            previousStatus = LightStatus.RED;
            status = LightStatus.YELLOW;
            currentDuration = 0;
        }
    }

    private void changeVisibleStatus() {
        double duration = currentDuration + flexibilityFactor;
        if (visibleStatus.equals(LightStatus.GREEN)) {
            if (duration < greenDelay) {
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
            if (duration < redDelay) {
                return;
            }
            previousVisibleStatus = LightStatus.RED;
            visibleStatus = LightStatus.YELLOW;
        }
    }

}
