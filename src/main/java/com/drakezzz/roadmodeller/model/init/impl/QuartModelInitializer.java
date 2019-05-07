package com.drakezzz.roadmodeller.model.init.impl;

import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.springframework.context.annotation.Primary;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


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
        modelState.setNetwork(buildNetwork());
        modelState.setMaxDuration(BigDecimal.valueOf(10000));
        modelState.setTimeDelta(BigDecimal.valueOf(1));
        modelState.setDrivers(new HashSet<>());
        modelState = trafficGenerator.generateTraffic(modelState);
        return modelState;
    }

    private List<RoadLane> buildNetwork() {
        List<RoadLane> network = new LinkedList<>();
        network.add(buildRoad(new Point(200,50), new Point(200, 760)));
        network.add(buildRoad(new Point(50,200), new Point(760, 200)));
        network.add(buildRoad(new Point(600,50), new Point(600, 760)));
        network.add(buildRoad(new Point(50,600), new Point(760, 600)));
        return network;
    }

}
