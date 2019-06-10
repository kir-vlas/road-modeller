package com.drakezzz.roadmodeller.model.processor.impl;

import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.processor.SimulationProcessor;
import com.drakezzz.roadmodeller.persistence.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.drakezzz.roadmodeller.persistence.entity.Driver.*;
import static com.drakezzz.roadmodeller.utils.VectorUtils.*;

@Slf4j
@Service
public class QuartScenarioSimulationProcessor implements SimulationProcessor {

    private static final Point SAFE_POINT_POSITIVE = new Point(1000, 1000);
    private static final Point SAFE_POINT_NEGATIVE = new Point(-1000, -1000);
    private static final double MIN_AVG_WAITING_CARS_TRIGGER = 3;

    private final TrafficGenerator trafficGenerator;

    public QuartScenarioSimulationProcessor(TrafficGenerator trafficGenerator) {
        this.trafficGenerator = trafficGenerator;
    }

    @Override
    public ModelState simulate(ModelState modelState) {
        if (modelState.getTime().compareTo(modelState.getMaxDuration()) < 0) {
            modelState.incrementStep();
            generateTraffic(modelState);
        }
        Set<Driver> drivers = modelState.getDrivers();
        if (modelState.getTime().compareTo(modelState.getMaxDuration()) < 0 || !drivers.isEmpty()) {
            List<TrafficLight> trafficLights = modelState.getTrafficLights();
            drivers.forEach(driver -> {

                Point speedVector = getDestinationVector(driver);
                speedVector = trafficLightStop(driver, speedVector, trafficLights);

                if (isDriverBrakes(drivers, driver, speedVector)) {
                    speedVector = calculateSpeed(driver, Car.getCurrentSpeed(driver.getCar(), false));
                } else {
                    speedVector = calculateSpeed(driver, Car.getCurrentSpeed(driver.getCar(), true));
                }
                if (speedVector.equals(ZERO)) {
                    driver.setIsWaitingGreenLight(true);
                } else {
                    driver.setIsWaitingGreenLight(false);
                }
                Point currentCoord = driver.getCurrentCoordinates();
                driver.setCurrentCoordinates(add(currentCoord, speedVector));

                if (Driver.isFinished(driver)) {
                    driver.setIsFinished(true);
                }
            });
            if (modelState.getIsTrafficLightsFlex()) {
                adaptLights(trafficLights);
            }
            trafficLights.forEach(TrafficLight::changeLight);
            checkCollisions(drivers);
            cleanCars(drivers);
            modelState.setDrivers(drivers.stream()
                    .filter(driver ->
                            !driver.getIsFinished())
                    .collect(Collectors.toSet()));
        } else {
            log.debug("Finished simulation [{}]", modelState.getUuid());
            modelState.setIsCompleted(true);
        }
        return modelState;
    }

    private void generateTraffic(ModelState modelState) {
        modelState = trafficGenerator.generateTraffic(modelState);
        checkCollisions(modelState.getDrivers());
        modelState.setDrivers(modelState.getDrivers().stream()
                .filter(driver ->
                        !driver.getIsFinished())
                .collect(Collectors.toSet()));
    }

    private void adaptLights(List<TrafficLight> trafficLights) {
        List<List<TrafficLight>> intersections = new LinkedList<>();
        for (TrafficLight trafficLight : trafficLights) {
            intersections.add(trafficLights.stream()
                    .filter(light ->
                            distance(light.getCoordinates(), trafficLight.getCoordinates()) < 20)
                    .collect(Collectors.toList()));
        }
        intersections.forEach(intersection -> {
            double avgWaitingCars = intersection.stream()
                    .mapToInt(trafficLight ->
                            trafficLight.getWaitingCars().size())
                    .filter(cnt -> cnt != 0)
                    .sum();
            if (avgWaitingCars < MIN_AVG_WAITING_CARS_TRIGGER) {
                return;
            }
            intersection.forEach(trafficLight ->
                    trafficLight.determineFlexibility(avgWaitingCars - MIN_AVG_WAITING_CARS_TRIGGER));
        });
    }

    private boolean isDriverBrakes(Set<Driver> drivers, Driver currentDriver, Point speedVector) {
        List<Driver> driversInSameRoad;
        if (speedVector.equals(ZERO)) {
            currentDriver.setIsWaitingGreenLight(true);
            return true;
        }
        driversInSameRoad = drivers.stream()
                .filter(driver ->
                        !currentDriver.equals(driver) &&
                                driver.getDestinationCoordinates().equals(currentDriver.getDestinationCoordinates()))
                .collect(Collectors.toList());
        if (driversInSameRoad.isEmpty()) {
            return false;
        }
        driversInSameRoad.sort((driver1, driver2) ->
                compareCoordinates(driver1.getCurrentCoordinates(), driver2.getCurrentCoordinates()));
        if (!speedVector.equals(ZERO)) {
            for (Driver driver : driversInSameRoad) {
                if (distance(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates()) < 25 &&
                        isBehind(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates(), speedVector)) {
                    currentDriver.setIsWaitingGreenLight(true);
                    return true;
                }
            }
        }

        currentDriver.setIsWaitingGreenLight(false);
        return false;
    }

    private Point trafficLightStop(Driver driver, Point speedVector, List<TrafficLight> trafficLightList) {

        long nearTrafficLightsCount = trafficLightList.stream()
                .filter(trafficLight ->
                        (trafficLight.getCoordinates().getX() == driver.getCurrentCoordinates().getX() ||
                                trafficLight.getCoordinates().getY() == driver.getCurrentCoordinates().getY()) &&
                                distance(trafficLight.getCoordinates(), driver.getCurrentCoordinates()) < 15 &&
                                !isBehind(driver.getCurrentCoordinates(), trafficLight.getCoordinates(), speedVector))
                .count();
        if (nearTrafficLightsCount == 1) {
            return new Point(speedVector);
        }

        for (TrafficLight trafficLight : trafficLightList) {
            if (distance(driver.getCurrentCoordinates(), trafficLight.getCoordinates()) > 15 ||
                    !isBehind(driver.getCurrentCoordinates(), trafficLight.getCoordinates(), speedVector)) {
                continue;
            }
            trafficLight.getWaitingCars().add(driver.getId());
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
                driver.setIsFinished(true);
            }
            if (isYLarger(currCoord, SAFE_POINT_POSITIVE)) {
                driver.setIsFinished(true);
            }
            if (isXLower(currCoord, SAFE_POINT_NEGATIVE)) {
                driver.setIsFinished(true);
            }
            if (isYLower(currCoord, SAFE_POINT_NEGATIVE)) {
                driver.setIsFinished(true);
            }
        });
    }

}
