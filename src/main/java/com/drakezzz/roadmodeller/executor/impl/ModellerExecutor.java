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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Predicate;

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
    public Mono<String> initAction(ModelSettings settings) {
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
        return modelRepositoryProvider.saveToDatabase(modelState)
                .map(ModelState::getUuid);
    }

    @Override
    public Mono<ModelState> executeAction(String actionId) {
        return modelRepositoryProvider.getModelState(actionId)
                .map(simulationProcessor::simulate)
                .onErrorResume(ex -> {
                    log.error("Error with model processing with id = [{}]. Stopping modelling. Error = {}", actionId, ex);
                    ModelState modelState = new ModelState();
                    modelState.setUuid(actionId);
                    modelState.setIsCompleted(true);
                    modelState.setIsFailed(true);
                    return Mono.just(modelState);
                })
                .flatMap(modelRepositoryProvider::saveToDatabase);
    }

    @Override
    public Mono<StatusResult> updateModel(String modelId, SettingsUpdate settingsUpdate) {
        return modelRepositoryProvider.getModelState(modelId)
                .map(modelState ->
                        updateModelState(modelState, settingsUpdate))
                .flatMap(modelRepositoryProvider::saveToDatabase)
                .map(modelState -> StatusResult.ok());
    }

    private ModelState updateModelState(ModelState modelState, SettingsUpdate settingsUpdate) {
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
        return modelState;
    }
}
