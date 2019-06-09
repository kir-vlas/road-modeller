package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.model.processor.SimulationProcessor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.entity.TrafficLight;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.SettingsUpdate;
import com.drakezzz.roadmodeller.web.dto.StatusResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Main action executor for modelling
 */
@Slf4j
@Service
@Primary
public class ModellerExecutor implements ContiniusActionExecutor {

    private final ModelRepositoryProvider modelRepositoryProvider;
    private final SimulationProcessor simulationProcessor;
    private final ModelInitializer modelInitializer;

    public ModellerExecutor(ModelRepositoryProvider modelRepositoryProvider, SimulationProcessor simulationProcessor, ModelInitializer modelInitializer) {
        this.modelRepositoryProvider = modelRepositoryProvider;
        this.simulationProcessor = simulationProcessor;
        this.modelInitializer = modelInitializer;
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
        modelState.setIsFailed(false);
        modelState.setStep(0);
        modelRepositoryProvider.saveToDatabase(modelState);
        String modelId = modelState.getUuid();
        log.debug("Initialized model with id = [{}]", modelId);
        return modelId;
    }

    @Override
    public ModelState executeAction(String actionId) {
        ModelState modelState = modelRepositoryProvider.getModelState(actionId);
        if (modelState.getIsFailed()) {
            return modelState;
        }
        try {
            modelState = simulationProcessor.simulate(modelState);
        } catch (RuntimeException ex) {
            log.error("Error with model processing with id = [{}]. Stopping modelling. Error = {}", actionId, ex);
            modelState.setIsCompleted(true);
            modelState.setIsFailed(true);
        }
        modelRepositoryProvider.saveToDatabase(modelState);
        return modelState;
    }

    @Override
    public StatusResult updateModel(String modelId, SettingsUpdate settingsUpdate) {
        ModelState modelState = modelRepositoryProvider.getModelState(modelId);
        modelState.getTrafficLights().forEach(trafficLight -> {
            trafficLight.setRedDelay(settingsUpdate.getRedDelay());
            trafficLight.setGreenDelay(settingsUpdate.getGreenDelay());
        });

        modelState.setIsTrafficLightsFlex(settingsUpdate.getIsAdaptive());
        if (!settingsUpdate.getIsAdaptive()) {
            TrafficLight def = modelState.getTrafficLights().get(0);
            double duration = def.getCurrentDuration();
            modelState.getTrafficLights().forEach(trafficLight -> {
                trafficLight.setFlexibilityFactor(NumberUtils.DOUBLE_ZERO);
                trafficLight.setCurrentDuration(duration);
            });
        }
        modelRepositoryProvider.saveToDatabase(modelState);
        return StatusResult.ok();
    }


}
