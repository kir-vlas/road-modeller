package com.drakezzz.roadmodeller.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.List;

@Data
public class RoadLane {

    @JsonIgnore
    private double length;

    @JsonProperty("hor")
    private Boolean isHorizontal = false;

    @JsonProperty("crds")
    private List<Point> coordinates;

    private RoadLane taper;

    private RoadLane up;

    private RoadLane down;

    private RoadLane left;

    private RoadLane right;

    @JsonIgnore
    private Boolean isCanTurnRight = false;

    @JsonIgnore
    private Boolean isCanTurnLeft = false;

    private List<Car> cars;

    @JsonIgnore
    private int destination = 0;

    @JsonIgnore
    private double maxSpeedLimit;

    @JsonIgnore
    private int trafficGeneratorFactor;

    public double calculateLegalMaxSpeedInMetersPerSec() {
        return maxSpeedLimit/3.6;
    }

}
