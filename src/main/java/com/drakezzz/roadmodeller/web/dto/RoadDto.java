package com.drakezzz.roadmodeller.web.dto;

import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoadDto {

    private double length;

    private boolean isHorizontal;

    private List<PointDto> coordinates;

    private double maxSpeedLimit;

    private int trafficGeneratorFactor;

    public RoadLane toRoadLane() {
        RoadLane roadLane = new RoadLane();
        roadLane.setLength(length);
        roadLane.setHorizontal(isHorizontal);
        roadLane.setCoordinates(coordinates.stream().map(PointDto::toPoint).collect(Collectors.toList()));
        roadLane.setMaxSpeedLimit(maxSpeedLimit);
        roadLane.setTrafficGeneratorFactor(trafficGeneratorFactor);
        return roadLane;
    }

}
