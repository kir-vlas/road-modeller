package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation("Инициализация модели")
    @PostMapping("/init")
    public ModelId initModel(@RequestBody @ApiParam("Настройки для инициализации модели") ModelSettings modelSettings) {
        String id = executor.initAction(modelSettings);
        return new ModelId(id);
    }

    @ApiOperation("Получение статистики модели")
    @GetMapping("/model/{modelId}")
    public StatisticEntity getStatistic(@PathVariable @ApiParam("Идентификатор модели") String modelId) {
        return statisticService.getStatistic(modelId);
    }

    @ApiOperation("Получение списка незавершенных моделей")
    @GetMapping("/models")
    public List<ModelId> getUnfinishedModelList() {
        return modelRepositoryProvider.getUnfinishedModels();
    }

    @ApiOperation("Удаление модели из базы данных")
    @DeleteMapping("/models/{modelId}")
    public void removeModel(@PathVariable @ApiParam("Идентификатор модели") String modelId) {
        modelRepositoryProvider.removeModel(modelId);
    }

}
