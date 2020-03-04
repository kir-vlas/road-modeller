package com.drakezzz.roadmodeller.persistence.entity;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.model.init.impl.QuartModelInitializer;
import com.drakezzz.roadmodeller.utils.VectorUtils;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.RoadDto;
import com.drakezzz.roadmodeller.web.dto.TrafficLightDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.drakezzz.roadmodeller.model.init.impl.QuartModelInitializer.buildLight;

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

    private Boolean isTrafficLightsFlex;

    private Boolean isDynamicTrafficFactor;

    private Boolean isFailed;

    private BigInteger overallCars = BigInteger.ZERO;

    public static ModelState of(ModelSettings settings) {
        ModelState modelState = new ModelState();
        modelState.setNetwork(settings.getNetwork().stream().map(RoadDto::toRoadLane).collect(Collectors.toList()));
        modelState.setDrivers(settings.getDrivers());
        modelState.setTrafficLights(settings.getTrafficLights().stream().map(TrafficLightDto::toTrafficLight).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(modelState.getTrafficLights())) {
            modelState.setTrafficLights(buildTrafficLights(modelState.getNetwork()));
        }
        modelState.setMaxDuration(settings.getMaxDuration());
        modelState.setTimeDelta(settings.getTimeDelta());
        modelState.setAttributes(settings.getAttributes());
        modelState.setIsDynamicTrafficFactor(settings.getIsDynamicFactor());
        modelState.setIsTrafficLightsFlex(Optional.ofNullable(settings.getIsFlex()).orElse(false));
        return modelState;
    }

    public void incrementStep() {
        step++;
        time = time.add(timeDelta);
    }

    public static List<TrafficLight> buildTrafficLights(List<RoadLane> network) {
        Set<TrafficLight> trafficLights = new HashSet<>();
        for (RoadLane lane1 : network) {
            network.forEach(lane2 -> VectorUtils.calculateIntersectionPoint(lane1, lane2)
                    .ifPresent(coord ->
                            trafficLights.add(buildLight(coord))
                    ));
        }
        List<RoadLane> horizontalRoads = network.stream()
                .filter(RoadLane::getIsHorizontal)
                .sorted((road1, road2) ->
                        VectorUtils.compareCoordinates(road1.getCoordinates().get(0), road2.getCoordinates().get(0)))
                .collect(Collectors.toList());
        for (int i = 0; i < horizontalRoads.size(); i++) {
            RoadLane roadLane = horizontalRoads.get(i);
            List<TrafficLight> roadLights = trafficLights.stream()
                    .filter(light ->
                            roadLane.getCoordinates().get(0).getY() == light.getCoordinates().getY())
                    .sorted((light1, light2) ->
                            VectorUtils.compareCoordinates(light1.getCoordinates(), light2.getCoordinates()))
                    .collect(Collectors.toList());
            List<List<TrafficLight>> intersections = new LinkedList<>();
            for (TrafficLight trafficLight : roadLights) {
                intersections.add(roadLights.stream()
                        .filter(light ->
                                VectorUtils.distance(light.getCoordinates(), trafficLight.getCoordinates()) < 45)
                        .collect(Collectors.toList()));
            }
            for (List<TrafficLight> interLights : intersections) {
                if (i >= interLights.size() / 2) {
                    for (int j = 0; j < interLights.size(); j++) {
                        if (j < interLights.size() / 2) {
                            interLights.get(j).setVisibleStatus(LightStatus.RED);
                        } else {
                            interLights.get(j).setVisibleStatus(LightStatus.GREEN);
                        }
                    }
                } else {
                    for (int j = 0; j < interLights.size(); j++) {
                        if (j < interLights.size() / 2) {
                            interLights.get(j).setVisibleStatus(LightStatus.GREEN);
                        } else {
                            interLights.get(j).setVisibleStatus(LightStatus.RED);
                        }
                    }
                }
            }
        }
        return new LinkedList<>(trafficLights);
    }
}