package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.List;

@Data
public class RoadLane {

    private double length;

    private boolean isHorizontal;

    private List<Point> coordinates;

    private RoadLane taper;

    private RoadLane up;

    private RoadLane down;

    private RoadLane left;

    private RoadLane right;

    private boolean isCanTurnRight;

    private boolean isCanTurnLeft;

    private List<Car> cars;

    private int destination = 0;

    private double maxSpeedLimit;

    private int trafficGeneratorFactor;

    public double getLegalMaxSpeedInMetersPerSec() {
        return maxSpeedLimit/3.6;
    }

}
