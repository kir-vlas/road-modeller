package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.model.generator.TrafficGenerator;
import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.springframework.context.annotation.Primary;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Primary
public class ModellerExecutor implements ContiniusActionExecutor {

    private final ModelRepositoryProvider modelRepositoryProvider;
    private final TrafficGenerator trafficGenerator;

    public ModellerExecutor(ModelRepositoryProvider modelRepositoryProvider, TrafficGenerator trafficGenerator) {
        this.modelRepositoryProvider = modelRepositoryProvider;
        this.trafficGenerator = trafficGenerator;
    }

    @Override
    public String initAction(ModelSettings settings) {
        ModelState modelState;
        if (settings.getIsNeedGenerate()) {
            modelState = trafficGenerator.generateTraffic();
        }
        else {
            modelState = ModelState.of(settings);
        }

        modelState.setUuid(UUID.randomUUID().toString());
        modelState.setStartTime(LocalDateTime.now());
        modelState.setTime(BigDecimal.ZERO);
        modelState.setIsCompleted(false);
        modelState.setStep(0);
        modelRepositoryProvider.saveToDatabase(modelState);
        return modelState.getUuid();
    }

    @Override
    public ModelState executeAction(String actionId) {
        ModelState modelState = modelRepositoryProvider.getModelState(actionId);
        if (modelState.getTime().compareTo(modelState.getMaxDuration()) < 0) {
            modelState.incrementStep();
            Set<Driver> drivers = modelState.getDrivers();
            drivers.forEach(driver -> {
                Point currentCoordinates = driver.getCurrentCoordinates();
                Point destCoordinates = driver.getDestinationCoordinates();
                if (compareCoordinates(currentCoordinates, destCoordinates) < 0) {
                    driver.setCurrentCoordinates(new Point(currentCoordinates.getX()+ driver.getCar().getSpeed(), currentCoordinates.getY()));
                }
            });

        } else {
            modelState.setIsCompleted(true);
        }
        modelRepositoryProvider.saveToDatabase(modelState);
        return modelState;
    }

    private int compareCoordinates(Point point1, Point point2) {
        if (point1.getY() < point2.getY()) return -1;
        if (point1.getY() > point2.getY()) return +1;
        if (point1.getX() < point2.getX()) return -1;
        if (point1.getX() > point2.getX()) return +1;
        return 0;
    }

}
