package com.drakezzz.roadmodeller.web.dto;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.persistence.entity.Driver;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ModelSettings {

    private Set<Driver> drivers;

    private List<RoadDto> network;

    private List<TrafficLightDto> trafficLights;

    private List<Attribute> attributes;

    private BigDecimal maxDuration;

    private BigDecimal timeDelta;

    private Boolean isNotInitialized;

}
