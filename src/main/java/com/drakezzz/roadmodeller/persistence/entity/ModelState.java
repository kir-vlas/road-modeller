package com.drakezzz.roadmodeller.persistence.entity;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
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

    private int trafficGeneratorFactor;

    public static ModelState of(ModelSettings settings) {
        ModelState modelState = new ModelState();
        modelState.setDrivers(settings.getDrivers());
        modelState.setNetwork(settings.getNetwork());
        modelState.setMaxDuration(settings.getMaxDuration());
        modelState.setTimeDelta(settings.getTimeDelta());
        modelState.setAttributes(settings.getAttributes());
        modelState.setTrafficGeneratorFactor(settings.getTrafficGeneratorFactor());

        return modelState;
    }

    public void incrementStep() {
        step++;
        time = time.add(timeDelta);
    }

}
