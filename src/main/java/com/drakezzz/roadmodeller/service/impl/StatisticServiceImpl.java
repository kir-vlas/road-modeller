package com.drakezzz.roadmodeller.service.impl;

import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.repository.StatisticEntityRepository;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final StatisticEntityRepository statisticEntityRepository;

    private final ModelRepositoryProvider modelRepositoryProvider;

    public StatisticServiceImpl(StatisticEntityRepository statisticEntityRepository, ModelRepositoryProvider modelRepositoryProvider) {
        this.statisticEntityRepository = statisticEntityRepository;
        this.modelRepositoryProvider = modelRepositoryProvider;
    }

    @Override
    public void collectStatistic(ModelState modelState) {
        StatisticEntity statisticEntity = getFullStatistic(modelState.getUuid());
        statisticEntity.getDrivers().addAll(modelState
                .getDrivers()
                .stream()
                .map(Driver::getId)
                .collect(Collectors.toSet())
        );
        statisticEntityRepository.save(statisticEntity);
    }

    @Override
    public StatisticEntity getFullStatistic(String modelId) {
        return statisticEntityRepository
                .findById(modelId)
                .orElseGet(() -> getEmpty(modelId));
    }

    @Override
    public StatisticEntity getShortStatistic(String modelId) {
        ModelState modelState = modelRepositoryProvider.getModelState(modelId);
        long waitingCars = modelState
                .getDrivers().stream()
                .filter(Driver::getIsWaitingGreenLight)
                .count();
        StatisticEntity statisticEntity = new StatisticEntity();
        statisticEntity.setAverageWaitingCars(BigDecimal.valueOf((double) waitingCars / ((double) modelState.getNetwork().size() / 2)));
        return statisticEntity;
    }

    private StatisticEntity getEmpty(String id) {
        StatisticEntity statisticEntity = new StatisticEntity();
        statisticEntity.setDrivers(new HashSet<>());
        statisticEntity.setId(id);
        return statisticEntity;
    }

}
