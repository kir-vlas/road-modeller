package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/models")
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
    public Mono<ModelId> initModel(@RequestBody ModelSettings modelSettings) {
        return executor.initAction(modelSettings)
                .map(ModelId::new);
    }

    @PutMapping("/{modelId}/update")
    public Mono<StatusResult> editModel(@PathVariable String modelId, @RequestBody SettingsUpdate settingsUpdate) {
        return executor.updateModel(modelId, settingsUpdate);
    }

    @GetMapping("/{modelId}")
    public Mono<StatisticEntity> getStatistic(@PathVariable String modelId) {
        return statisticService.getFullStatistic(modelId);
    }

    @GetMapping("/{modelId}/short")
    public Mono<StatisticEntity> getShortStatistic(@PathVariable String modelId) {
        return statisticService.getShortStatistic(modelId);
    }

    @GetMapping
    public Flux<ModelId> getUnfinishedModelList() {
        return modelRepositoryProvider.getUnfinishedModels();
    }

    @DeleteMapping("/{modelId}")
    public Mono<Void> removeModel(@PathVariable String modelId) {
        return modelRepositoryProvider.removeModel(modelId);
    }


}
