package com.drakezzz.roadmodeller.persistence.entity;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.RoadDto;
import com.drakezzz.roadmodeller.web.dto.TrafficLightDto;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ModelState {

    @Id
    private String uuid;

    private Set<Driver> drivers;

    private Integer step;

    private BigDecimal time;

    private BigDecimal timeDelta;

    private BigDecimal maxDuration;

    private LocalDateTime startTime;

    private List<Car> cars;

    private List<RoadLane> network;

    private List<TrafficLight> trafficLights;

    private List<Sidewalk> sidewalks;

    private List<Pedestrian> pedestrians;

    private List<Attribute> attributes;

    private Boolean isCompleted;

    private Boolean isFailed;

    public static ModelState of(ModelSettings settings) {
        ModelState modelState = new ModelState();
        modelState.setNetwork(settings.getNetwork().stream().map(RoadDto::toRoadLane).collect(Collectors.toList()));
        modelState.setDrivers(settings.getDrivers());
        modelState.setTrafficLights(settings.getTrafficLights().stream().map(TrafficLightDto::toTrafficLight).collect(Collectors.toList()));
        modelState.setMaxDuration(settings.getMaxDuration());
        modelState.setTimeDelta(settings.getTimeDelta());
        modelState.setAttributes(settings.getAttributes());
        return modelState;
    }

    public void incrementStep() {
        step++;
        time = time.add(timeDelta);
    }

}
