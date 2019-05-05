package com.drakezzz.roadmodeller.model.generator.impl;

import com.drakezzz.roadmodeller.model.DriverModel;
import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.impl.IntelligentDriverModel;
import com.drakezzz.roadmodeller.model.random.RandomProvider;
import com.drakezzz.roadmodeller.persistence.entity.*;
import org.apache.commons.lang3.RandomUtils;
import org.mockito.internal.util.collections.Sets;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        Driver driver = new Driver();
        driver.setId(UUID.randomUUID().toString());
        driver.setAttributeList(new ArrayList<>());
        driver.setCar(buildCar(network));
        Point originPoint1 = new Point(10,500);
        Point destPoint1 = new Point(900,500);
        Point originPoint2 = new Point(900,512);
        Point destPoint2 = new Point(10,512);
        if (RandomUtils.nextInt(1,100) < 50) {
            driver.setOriginCoordinates(originPoint1);
            driver.setCurrentCoordinates(originPoint1);
            driver.setDestinationCoordinates(destPoint1);
        } else {
            driver.setOriginCoordinates(originPoint2);
            driver.setCurrentCoordinates(originPoint2);
            driver.setDestinationCoordinates(destPoint2);
        }
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
