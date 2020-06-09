package com.drakezzz.roadmodeller.service.impl;

import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.repository.StatisticEntityRepository;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashSet;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final StatisticEntityRepository statisticEntityRepository;

    private final ModelRepositoryProvider modelRepositoryProvider;

    public StatisticServiceImpl(StatisticEntityRepository statisticEntityRepository, ModelRepositoryProvider modelRepositoryProvider) {
        this.statisticEntityRepository = statisticEntityRepository;
        this.modelRepositoryProvider = modelRepositoryProvider;
    }

    @Override
    public Mono<StatisticEntity> getFullStatistic(String modelId) {
        return statisticEntityRepository
                .findById(modelId)
                .switchIfEmpty(getEmpty(modelId));
    }

    @Override
    public Mono<StatisticEntity> getShortStatistic(String modelId) {
        return getFullStatistic(modelId)
                .zipWith(modelRepositoryProvider.getModelState(modelId))
                .map(tuple -> {
                    StatisticEntity statisticEntity = tuple.getT1();
                    ModelState modelState = tuple.getT2();
                    long waitingCars = modelState
                            .getDrivers().stream()
                            .filter(Driver::getIsWaitingGreenLight)
                            .count();
                    BigDecimal averageWaitingCars = BigDecimal.valueOf((double) waitingCars / ((double) modelState.getNetwork().size() / 2));
                    statisticEntity.getAverageWaitingTime().add(averageWaitingCars);
                    statisticEntity.setAverageWaitingCars(averageWaitingCars);
                    statisticEntity.setOverallCarsCount(modelState.getOverallCars());
                    return statisticEntity;
                })
                .flatMap(statisticEntityRepository::save);
    }

    private Mono<StatisticEntity> getEmpty(String id) {
        StatisticEntity statisticEntity = new StatisticEntity();
        statisticEntity.setDrivers(new HashSet<>());
        statisticEntity.setId(id);
        return Mono.just(statisticEntity);
    }

}
