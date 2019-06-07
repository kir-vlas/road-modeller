package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.model.init.ModelInitializer;
import com.drakezzz.roadmodeller.model.processor.SimulationProcessor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import lombok.extern.slf4j.Slf4j;
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
    private final StatisticService statisticService;

    public ModellerExecutor(ModelRepositoryProvider modelRepositoryProvider, SimulationProcessor simulationProcessor, ModelInitializer modelInitializer, StatisticService statisticService) {
        this.modelRepositoryProvider = modelRepositoryProvider;
        this.simulationProcessor = simulationProcessor;
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
            log.error("Error with model processing with id = [{}]. Stopping modelling", actionId);
            modelState.setIsCompleted(true);
            modelState.setIsFailed(true);
        }
        statisticService.collectStatistic(modelState);
        modelRepositoryProvider.saveToDatabase(modelState);
        return modelState;
    }



}
