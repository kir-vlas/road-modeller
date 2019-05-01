package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class ModelState {

    @Id
    private String uuid;

    private Set<Driver> drivers;

    private int step;

    private BigDecimal time;

    private BigDecimal timeDelta;

    private BigDecimal maxDuration;

    private LocalDateTime startTime;

    private List<Car> cars;

    private List<RoadLane> network;

    private List<TrafficLight> trafficLights;

    private List<Sidewalk> sidewalks;

    private List<Pedestrian> pedestrians;

}
