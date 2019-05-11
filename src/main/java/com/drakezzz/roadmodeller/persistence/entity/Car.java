package com.drakezzz.roadmodeller.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Duration;
import java.time.LocalTime;

@Data
public class Car {

    private double speed;

    @JsonIgnore
    private double speedScale;

    private double acceleration;

    private double deceleration;

    private double maxSpeed;

    private double distanceThreshold;

    private Direction direction;

    private boolean isCanChangeRoadLane;

    private boolean isRefreshed;

    private boolean isLeader;

    private boolean waitGreenLight;

    private Duration waitingTime;

    private LocalTime waitStartTime;

    private RoadLane currentLane;

}
