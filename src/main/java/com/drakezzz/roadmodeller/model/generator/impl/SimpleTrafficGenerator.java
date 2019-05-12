package com.drakezzz.roadmodeller.model.generator.impl;

import com.drakezzz.roadmodeller.model.DriverModel;
import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.impl.IntelligentDriverModel;
import com.drakezzz.roadmodeller.model.random.RandomProvider;
import com.drakezzz.roadmodeller.persistence.entity.*;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


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
    public ModelState generateTraffic(ModelState modelState) {
        modelState.getDrivers().addAll(createDrivers(modelState.getNetwork()));
        modelState.setCars(modelState.getDrivers().stream().map(Driver::getCar).collect(Collectors.toList()));
        return modelState;
    }

    private Set<Driver> createDrivers(List<RoadLane> network) {
       Set<Driver> newDrivers = new HashSet<>();
        network.forEach(road -> {
            if (RandomUtils.nextInt(1,1000) < road.getTrafficGeneratorFactor()) {
                Driver driver = new Driver();
                driver.setCurrentRoad(road);
                driver.setId(UUID.randomUUID().toString());
                driver.setAttributeList(new ArrayList<>());
                driver.setCar(buildCar(network));
                Point originPoint1 = road.getCoordinates().get(0);
                Point destPoint1 = road.getCoordinates().get(1);
                driver.setOriginCoordinates(originPoint1);
                driver.setCurrentCoordinates(originPoint1);
                driver.setDestinationCoordinates(destPoint1);
                newDrivers.add(driver);
            }
        });
        return newDrivers;
    }

    private Car buildCar(List<RoadLane> network) {
        Car car = new Car();
        car.setSpeed(0);
        car.setMaxSpeed(60);
        car.setAcceleration(10);
        car.setDeceleration(10);
        car.setSpeedScale(30);
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
