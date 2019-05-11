package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.LightStatus;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.entity.TrafficLight;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.drakezzz.roadmodeller.persistence.entity.Driver.calculateSpeed;
import static com.drakezzz.roadmodeller.persistence.entity.Driver.checkCollisions;
import static com.drakezzz.roadmodeller.utils.VectorUtils.*;

@Service
@Primary
public class ModellerExecutor implements ContiniusActionExecutor {

    private static final Point SAFE_POINT_POSITIVE = new Point(1000, 1000);
    private static final Point SAFE_POINT_NEGATIVE = new Point(-1000, -1000);

    private final ModelRepositoryProvider modelRepositoryProvider;
    private final TrafficGenerator trafficGenerator;
    private final ModelInitializer modelInitializer;
    private final StatisticService statisticService;

    public ModellerExecutor(ModelRepositoryProvider modelRepositoryProvider, TrafficGenerator trafficGenerator, ModelInitializer modelInitializer, StatisticService statisticService) {
        this.modelRepositoryProvider = modelRepositoryProvider;
        this.trafficGenerator = trafficGenerator;
        this.modelInitializer = modelInitializer;
        this.statisticService = statisticService;
    }

    @Override
    public String initAction(ModelSettings settings) {
        ModelState modelState;
        if (settings.getIsNotInitialized()) {
            modelState = modelInitializer.initializeModel(settings);
        } else {
            modelState = ModelState.of(settings);
        }

        modelState.setUuid(UUID.randomUUID().toString());
        modelState.setStartTime(LocalDateTime.now());
        modelState.setTime(BigDecimal.ZERO);
        modelState.setIsCompleted(false);
        modelState.setStep(0);
        modelState.setTrafficGeneratorFactor(10);
        modelRepositoryProvider.saveToDatabase(modelState);
        return modelState.getUuid();
    }

    @Override
    public ModelState executeAction(String actionId) {
        ModelState modelState = modelRepositoryProvider.getModelState(actionId);
        if (modelState.getTime().compareTo(modelState.getMaxDuration()) < 0) {
            modelState.incrementStep();
            if (RandomUtils.nextInt(1, 100) < modelState.getTrafficGeneratorFactor()) {
                modelState = trafficGenerator.generateTraffic(modelState);
                checkCollisions(modelState.getDrivers());
                modelState.setDrivers(modelState.getDrivers().stream()
                        .filter(driver ->
                                !driver.isFinished())
                        .collect(Collectors.toSet()));
            }
            Set<Driver> drivers = modelState.getDrivers();
            List<TrafficLight> trafficLights = modelState.getTrafficLights();
            drivers.forEach(driver -> {
                Point speedVector = calculateSpeed(driver);
                speedVector = trafficLightStop(driver, speedVector, trafficLights);
                if (nearCars(drivers, driver, trafficLights)) {
                    speedVector = ZERO;
                }
                if (!speedVector.equals(ZERO)) {
                    Point currentCoord = driver.getCurrentCoordinates();
                    driver.setCurrentCoordinates(add(currentCoord, speedVector));
                }
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
        statisticService.collectStatistic(modelState);
        modelRepositoryProvider.saveToDatabase(modelState);
        return modelState;
    }

    private boolean nearCars(Set<Driver> drivers, Driver currentDriver, List<TrafficLight> trafficLights) {
        List<Driver> driversInSameRoad;
        Point speedVector2 = calculateSpeed(currentDriver);
        if (trafficLightStop(currentDriver, speedVector2, trafficLights).equals(ZERO)) {
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
        if (!trafficLightStop(currentDriver, speedVector2, trafficLights).equals(ZERO)) {
            for (Driver driver : driversInSameRoad) {
                if (!currentDriver.equals(driver) && distance(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates()) < 25 && isBehind(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates(), speedVector2)) {
                    currentDriver.setWaitingGreenLight(true);
                    return true;
                }
            }
        }


//        for (Driver driver : driversInSameRoad) {
//            if (!currentDriver.equals(driver) && distance(currentDriver.getCurrentCoordinates(), driver.getCurrentCoordinates()) < 35 && driver.isWaitingGreenLight()) {
//                currentDriver.setWaitingGreenLight(true);
//                return true;
//            }
//        }

        currentDriver.setWaitingGreenLight(false);
        return false;
    }

    private boolean isBehind(Point point1, Point point2, Point speedVector) {
        if (compareCoordinates(speedVector, ZERO) > 0) {
            return compareCoordinates(point1, point2) < 0;
        } else {
            return compareCoordinates(point1, point2) > 0;
        }
    }

    private Point trafficLightStop(Driver driver, Point speedVector, List<TrafficLight> trafficLightList) {
        for (TrafficLight trafficLight : trafficLightList) {
            if (distance(driver.getCurrentCoordinates(), trafficLight.getCoordinates()) > 9) {
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
