package com.drakezzz.roadmodeller.model.init.impl;

import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple model builder with one road and one driver which moves directly to destination point
 */
@Service
public class SimpleModelInitializer implements ModelInitializer {
    private final TrafficGenerator trafficGenerator;

    public SimpleModelInitializer(TrafficGenerator trafficGenerator) {
        this.trafficGenerator = trafficGenerator;
    }

    @Override
    public ModelState initializeModel(ModelSettings settings) {
        ModelState modelState = new ModelState();
        List<RoadLane> network = buildNetwork();
        modelState.setNetwork(network);
        modelState.setMaxDuration(BigDecimal.valueOf(5000));
        modelState.setTimeDelta(BigDecimal.valueOf(1));
        modelState.setDrivers(new HashSet<>());
        modelState = trafficGenerator.generateTraffic(modelState);
        return modelState;
    }

    private List<RoadLane> buildNetwork() {
        List<RoadLane> network = new LinkedList<>();
        network.add(buildRoad(new Point(5,500), new Point(905, 500)));
        network.add(buildRoad(new Point(5,512), new Point(905, 512)));
        return network;
    }

    private RoadLane buildRoad(Point origin, Point dest) {
        RoadLane roadLane = new RoadLane();
        roadLane.setMaxSpeedLimit(60);
        List<Point> coordinates = new ArrayList<>();
        coordinates.add(origin);
        coordinates.add(dest);
        roadLane.setCoordinates(coordinates);
        roadLane.setLength(900);
        roadLane.setCanTurnLeft(false);
        roadLane.setCanTurnRight(false);
        return roadLane;
    }

}

