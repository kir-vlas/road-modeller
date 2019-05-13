package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ModellerController {

    private final ContiniusActionExecutor executor;
    private final StatisticService statisticService;
    private final ModelRepositoryProvider modelRepositoryProvider;

    public ModellerController(ContiniusActionExecutor executor, StatisticService statisticService, ModelRepositoryProvider modelRepositoryProvider) {
        this.executor = executor;
        this.statisticService = statisticService;
        this.modelRepositoryProvider = modelRepositoryProvider;
    }

    @PostMapping("/init")
    public ModelId initModel(@RequestBody ModelSettings modelSettings) {
        String id = executor.initAction(modelSettings);
        return new ModelId(id);
    }

    @GetMapping("/model/{modelId}")
    public StatisticEntity getStatistic(@PathVariable String modelId) {
        return statisticService.getStatistic(modelId);
    }

    @GetMapping("/models")
    public List<ModelId> getUnfinishedModelList() {
        return modelRepositoryProvider.getUnfinishedModels();
    }

    @DeleteMapping("/models/{modelId}")
    public void removeModel(@PathVariable String modelId) {
        modelRepositoryProvider.removeModel(modelId);
    }

}
