package com.drakezzz.roadmodeller.model.init.impl;

import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.persistence.entity.LightStatus;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import com.drakezzz.roadmodeller.persistence.entity.TrafficLight;
import com.drakezzz.roadmodeller.utils.VectorUtils;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.QuartTrafficGenerate;
import org.springframework.context.annotation.Primary;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Model initializer for quart road model for traffic lights optimization task
 */
@Service
@Primary
public class QuartModelInitializer implements ModelInitializer {

    private final TrafficGenerator trafficGenerator;

    public QuartModelInitializer(TrafficGenerator trafficGenerator) {
        this.trafficGenerator = trafficGenerator;
    }

    @Override
    public ModelState initializeModel(ModelSettings settings) {
        ModelState modelState = new ModelState();
        modelState.setNetwork(buildNetwork(settings.getTrafficGenerate()));
        modelState.setMaxDuration(BigDecimal.valueOf(10000));
        modelState.setTimeDelta(BigDecimal.valueOf(1));
        modelState.setTrafficLights(buildTrafficLights(modelState.getNetwork()));
        modelState.setDrivers(new HashSet<>());
        modelState.setIsTrafficLightsFlex(settings.getIsFlex());
        modelState = trafficGenerator.generateTraffic(modelState);
        return modelState;
    }

    private List<RoadLane> buildNetwork(QuartTrafficGenerate trafficGenerationFactor) {
        List<RoadLane> network = new LinkedList<>();
        network.add(buildRoad(new Point(210,760), new Point(210, 50), trafficGenerationFactor.getRoad1()));
        network.add(buildRoad(new Point(200, 50), new Point(200,760), trafficGenerationFactor.getRoad2()));
        network.add(buildRoad(new Point(50,210), new Point(760, 210), trafficGenerationFactor.getRoad3()));
        network.add(buildRoad(new Point(760, 200), new Point(50,200), trafficGenerationFactor.getRoad4()));
        network.add(buildRoad(new Point(610,760), new Point(610, 50), trafficGenerationFactor.getRoad5()));
        network.add(buildRoad(new Point(600, 50), new Point(600,760), trafficGenerationFactor.getRoad6()));
        network.add(buildRoad(new Point(50,610), new Point(760, 610), trafficGenerationFactor.getRoad7()));
        network.add(buildRoad(new Point(760, 600), new Point(50,600), trafficGenerationFactor.getRoad8()));
        return network;
    }

    private List<TrafficLight> buildTrafficLights(List<RoadLane> network) {
        Set<TrafficLight> trafficLights = new HashSet<>();
        for (RoadLane lane1: network) {
            network.forEach(lane2 -> VectorUtils.calculateIntersectionPoint(lane1, lane2)
                    .ifPresent(coord ->
                            trafficLights.add(buildLight(coord))
                    ));
        }
        List<RoadLane> horizontalRoads = network.stream()
                .filter(RoadLane::getIsHorizontal)
                .collect(Collectors.toList());
        for (int i = 0; i < horizontalRoads.size(); i++) {
            RoadLane roadLane = horizontalRoads.get(i);
            List<TrafficLight> roadLights = trafficLights.stream()
                    .filter(light ->
                            roadLane.getCoordinates().get(0).getY() == light.getCoordinates().getY())
                    .sorted((light1, light2) ->
                            VectorUtils.compareCoordinates(light1.getCoordinates(), light2.getCoordinates()))
                    .collect(Collectors.toList());
            if (i % 2 == 0) {
                for (int j = 0; j < roadLights.size(); j++) {
                    if (j % 2 == 0) {
                        roadLights.get(j).setVisibleStatus(LightStatus.RED);
                    } else {
                        roadLights.get(j).setVisibleStatus(LightStatus.GREEN);
                    }
                }
            } else {
                for (int j = 0; j < roadLights.size(); j++) {
                    if (j % 2 == 0) {
                        roadLights.get(j).setVisibleStatus(LightStatus.GREEN);
                    } else {
                        roadLights.get(j).setVisibleStatus(LightStatus.RED);
                    }
                }
            }
        }
        return new LinkedList<>(trafficLights);
    }

    private TrafficLight buildLight(Point coord) {
        TrafficLight trafficLight = new TrafficLight();
        trafficLight.setCoordinates(coord);
        trafficLight.setStatus(LightStatus.RED);
        trafficLight.setRedDelay(300);
        trafficLight.setGreenDelay(300);
        trafficLight.setCurrentDuration(0);
        trafficLight.setMaxFlexibility(200);
        return trafficLight;
    }

}
