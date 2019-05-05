package com.drakezzz.roadmodeller.web.dto;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.persistence.entity.*;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ModelDto {

    private Set<Driver> drivers;

    private List<RoadLane> network;

    private List<TrafficLight> trafficLights;

    private List<Sidewalk> sidewalks;

    private List<Pedestrian> pedestrians;

    private List<Attribute> attributes;

    private Boolean isCompleted;

    public static ModelDto of(ModelState modelState) {
        ModelDto modelDto = new ModelDto();
        modelDto.setDrivers(modelState.getDrivers().stream().peek(driver -> driver.setCar(null)).collect(Collectors.toSet()));
        modelDto.setNetwork(modelState.getNetwork());
        modelDto.setTrafficLights(modelState.getTrafficLights());
        modelDto.setAttributes(modelState.getAttributes());
        modelDto.setIsCompleted(modelState.getIsCompleted());
        return modelDto;
    }

}
