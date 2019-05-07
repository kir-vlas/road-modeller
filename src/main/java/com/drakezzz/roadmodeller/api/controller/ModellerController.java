package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ModellerController {

    private final ContiniusActionExecutor executor;
    private final StatisticService statisticService;

    public ModellerController(ContiniusActionExecutor executor, StatisticService statisticService) {
        this.executor = executor;
        this.statisticService = statisticService;
    }

    @GetMapping("/init")
    public ModelId initModel() {
        ModelSettings modelSettings = new ModelSettings();
        modelSettings.setIsNotInitialized(true);
        String id = executor.initAction(modelSettings);
        return new ModelId(id);
    }

    @GetMapping("/model/{modelId}")
    public StatisticEntity getStatistic(@PathVariable String modelId) {
        return statisticService.getStatistic(modelId);
    }

}
