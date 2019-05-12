package com.drakezzz.roadmodeller.model.processor.impl;

import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.processor.SimulationProcessor;
import com.drakezzz.roadmodeller.persistence.entity.*;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.drakezzz.roadmodeller.persistence.entity.Driver.*;
import static com.drakezzz.roadmodeller.utils.VectorUtils.*;

@Service
public class QuartScenarioSimulationProcessor implements SimulationProcessor {

    private static final Point SAFE_POINT_POSITIVE = new Point(1000, 1000);
    private static final Point SAFE_POINT_NEGATIVE = new Point(-1000, -1000);

    private final TrafficGenerator trafficGenerator;

    public QuartScenarioSimulationProcessor(TrafficGenerator trafficGenerator) {
        this.trafficGenerator = trafficGenerator;
    }

    @Override
    public ModelState simulate(ModelState modelState) {
        if (modelState.getTime().compareTo(modelState.getMaxDuration()) < 0) {
            modelState.incrementStep();
            generateTraffic(modelState);
            Set<Driver> drivers = modelState.getDrivers();
            List<TrafficLight> trafficLights = modelState.getTrafficLights();
            drivers.forEach(driver -> {

                Point speedVector = getDestinationVector(driver);
                speedVector = trafficLightStop(driver, speedVector, trafficLights);

                if (isDriverBrakes(drivers, driver, speedVector)) {
                    speedVector = calculateSpeed(driver, Car.getCurrentSpeed(driver.getCar(), false));
                } else {
                    speedVector = calculateSpeed(driver, Car.getCurrentSpeed(driver.getCar(), true));
                }
                Point currentCoord = driver.getCurrentCoordinates();
                driver.setCurrentCoordinates(add(currentCoord, speedVector));

                if (Driver.isFinished(driver)) {
                    driver.setFinished(true);
                }
            });
            trafficLights.forEach(TrafficLight::changeLight);
            checkCollisions(drivers);
            cleanCars(drivers);
            modelState.setDrivers(drivers.stream()
                    .filter(driver ->
                            !driver.isFinished())
                    .collect(Collectors.toSet()));
        } else {
            modelState.setIsCompleted(true);
        }
        return modelState;
    }

    private void generateTraffic(ModelState modelState) {
        modelState = trafficGenerator.generateTraffic(modelState);
        checkCollisions(modelState.getDrivers());
        modelState.setDrivers(modelState.getDrivers().stream()
                .filter(driver ->
                        !driver.isFinished())
                .collect(Collectors.toSet()));
    }

    private boolean isDriverBrakes(Set<Driver> drivers, Driver currentDriver, Point speedVector) {
        List<Driver> driversInSameRoad;
        if (speedVector.equals(ZERO)) {
            currentDriver.setWaitingGreenLight(true);
            return true;
        }
        driversInSameRoad = drivers.stream()
                .filter(driver ->
                        driver.getDestinationCoordinates().equals(currentDriver.getDestinationCoordinates()))
                .collect(Collectors.toList());
        if (driversInSameRoad.isEmpty()) {
            return false;
        }
        driversInSameRoad.sort((driver1, driver2) ->
                compareCoordinates(driver1.getCurrentCoordinates(), driver2.getCurrentCoordinates()));
        if (!speedVector.equals(ZERO)) {
            for (Driver driver : driversInSameRoad) {
                if (!currentDriver.equals(driver) && distance(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates()) < 25 && isBehind(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates(), speedVector)) {
                    currentDriver.setWaitingGreenLight(true);
                    return true;
                }
            }
        }

        currentDriver.setWaitingGreenLight(false);
        return false;
    }

    private Point trafficLightStop(Driver driver, Point speedVector, List<TrafficLight> trafficLightList) {

        long nearTrafficLightsCount = trafficLightList.stream()
                .filter(trafficLight ->
                        (trafficLight.getCoordinates().getX() == driver.getCurrentCoordinates().getX() ||
                                trafficLight.getCoordinates().getY() == driver.getCurrentCoordinates().getY()) &&
                                distance(trafficLight.getCoordinates(), driver.getCurrentCoordinates()) < 40)
                .count();
        if (nearTrafficLightsCount < 2) {
            return new Point(speedVector);
        }

        for (TrafficLight trafficLight : trafficLightList) {
            if (distance(driver.getCurrentCoordinates(), trafficLight.getCoordinates()) > 20 ||
                    !isBehind(driver.getCurrentCoordinates(), trafficLight.getCoordinates(), speedVector)) {
                continue;
            }
            if (LightStatus.RED.equals(trafficLight.getStatus())) {
                return new Point(0, speedVector.getY());
            }
            if (LightStatus.GREEN.equals(trafficLight.getStatus())) {
                return new Point(speedVector.getX(), 0);
            }
            if (LightStatus.YELLOW.equals(trafficLight.getStatus())) {
                return ZERO;
            }
        }
        return new Point(speedVector);
    }

    private void cleanCars(Set<Driver> drivers) {
        drivers.forEach(driver -> {
            Point currCoord = driver.getCurrentCoordinates();
            if (isXLarger(currCoord, SAFE_POINT_POSITIVE)) {
                driver.setFinished(true);
            }
            if (isYLarger(currCoord, SAFE_POINT_POSITIVE)) {
                driver.setFinished(true);
            }
            if (isXLower(currCoord, SAFE_POINT_NEGATIVE)) {
                driver.setFinished(true);
            }
            if (isYLower(currCoord, SAFE_POINT_NEGATIVE)) {
                driver.setFinished(true);
            }
        });
    }

}
