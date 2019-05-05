package com.drakezzz.roadmodeller.model.generator.impl;

import com.drakezzz.roadmodeller.model.DriverModel;
import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.impl.IntelligentDriverModel;
import com.drakezzz.roadmodeller.model.random.RandomProvider;
import com.drakezzz.roadmodeller.persistence.entity.*;
import org.mockito.internal.util.collections.Sets;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple model builder with one road and one driver which moves directly to destination point
 */
@Service
public class SimpleTrafficGenerator implements TrafficGenerator {

    private static final double DISTRIBUTION = 1;
    private static final double DEVIATION = 0.2;

    private final DriverModel driverModel;
    private final RandomProvider randomProvider;

    public SimpleTrafficGenerator(DriverModel driverModel, RandomProvider randomProvider) {
        this.driverModel = driverModel;
        this.randomProvider = randomProvider;
    }

    @Override
    public ModelState generateTraffic() {
        ModelState modelState = new ModelState();
        List<RoadLane> network = buildNetwork();
        modelState.setNetwork(network);
        modelState.setDrivers(createDrivers(network));
        modelState.setMaxDuration(BigDecimal.valueOf(5000));
        modelState.setTimeDelta(BigDecimal.valueOf(5));
        modelState.setCars(modelState.getDrivers().stream().map(Driver::getCar).collect(Collectors.toList()));
        return modelState;
    }

    private List<RoadLane> buildNetwork() {
        RoadLane roadLane = new RoadLane();
        roadLane.setMaxSpeedLimit(60);
        List<Point> coordinates = new ArrayList<>();
        coordinates.add(new Point(5,5));
        coordinates.add(new Point(5005,5));
        roadLane.setCoordinates(coordinates);
        roadLane.setLength(5000);
        roadLane.setCanTurnLeft(false);
        roadLane.setCanTurnRight(false);
        return Collections.singletonList(roadLane);
    }

    private Set<Driver> createDrivers(List<RoadLane> network) {
        Driver driver = new Driver();
        driver.setAttributeList(new ArrayList<>());
        driver.setCar(buildCar(network));
        Point point = new Point(10,5);
        driver.setCurrentCoordinates(point);
        driver.setOriginCoordinates(point);
        driver.setDestinationCoordinates(new Point(4005,5));
        appendStochasticParameters(driver);
        return Sets.newSet(driver);
    }

    private Car buildCar(List<RoadLane> network) {
        Car car = new Car();
        car.setSpeed(2);
        car.setCurrentLane(network.get(0));
        car.setDirection(Direction.DIRECT);
        car.setCanChangeRoadLane(false);
        return car;
    }

    private void appendStochasticParameters(Driver driver) {
        if (driverModel instanceof IntelligentDriverModel) {
            driver.setAttribute(IntelligentDriverModel.V0, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
            driver.setAttribute(IntelligentDriverModel.T, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
            driver.setAttribute(IntelligentDriverModel.A, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
            driver.setAttribute(IntelligentDriverModel.B, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
            driver.setAttribute(IntelligentDriverModel.S0, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
            driver.setAttribute(IntelligentDriverModel.F_SPEED, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
            driver.setAttribute(IntelligentDriverModel.DELTA, randomProvider.getRandomNumber(DISTRIBUTION, DEVIATION));
        }
    }

}
