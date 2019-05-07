package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        }
        else {
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
            if (RandomUtils.nextInt(1,100) < modelState.getTrafficGeneratorFactor()) {
                modelState = trafficGenerator.generateTraffic(modelState);
            }
            Set<Driver> drivers = modelState.getDrivers();
            drivers.forEach(driver -> {
                Point speedVector = calculateSpeed(driver);
                if (speedVector.equals(ZERO)) {
                    driver.setFinished(true);
                } else {
                    Point currentCoord = driver.getCurrentCoordinates();
                    driver.setCurrentCoordinates(add(currentCoord, speedVector));
                }
            });
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

    private Point calculateSpeed(Driver driver) {
        Point currentCoordinates = driver.getCurrentCoordinates();
        Point destCoordinates = driver.getDestinationCoordinates();
        if (currentCoordinates.equals(destCoordinates)) {
            return ZERO;
        }
        if (currentCoordinates.getX() != destCoordinates.getX()) {
            if (isXLower(currentCoordinates, destCoordinates)) {
                return new Point(driver.getCar().getSpeed(), 0);
            } else {
                return new Point(-driver.getCar().getSpeed(), 0);
            }
        }
        else {
            if (isYLower(currentCoordinates, destCoordinates)) {
                return new Point(0, driver.getCar().getSpeed());
            } else {
                return new Point(0, -driver.getCar().getSpeed());
            }
        }
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

    private void checkCollisions(Set<Driver> drivers) {
        for (Driver driver1 : drivers) {
            drivers.forEach(driver2 -> {
                if (driver1.equals(driver2)) {
                    return;
                }
                Point speedVector1 = calculateSpeed(driver1);
                Point speedVector2 = calculateSpeed(driver2);
                if (speedVector1.equals(speedVector2) && distance(driver1.getCurrentCoordinates(), driver2.getCurrentCoordinates()) < 20) {
                    driver1.setFinished(true);
                }
            });
        }
    }

}
