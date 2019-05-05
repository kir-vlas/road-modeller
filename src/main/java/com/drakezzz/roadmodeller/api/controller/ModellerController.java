package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ModellerController {

    private final ContiniusActionExecutor executor;
    private final ModelRepositoryProvider modelRepositoryProvider;

    public ModellerController(ContiniusActionExecutor executor, ModelRepositoryProvider modelRepositoryProvider) {
        this.executor = executor;
        this.modelRepositoryProvider = modelRepositoryProvider;
    }

    @GetMapping("/init")
    public ModelId initModel() {
        ModelSettings modelSettings = new ModelSettings();
        modelSettings.setIsNotInitialized(true);
        String id = executor.initAction(modelSettings);
        return new ModelId(id);
    }

    @GetMapping("/model/{modelId}")
    public ModelState getStatistic(@PathVariable String modelId) {
        return modelRepositoryProvider.getModelState(modelId);
    }

}
