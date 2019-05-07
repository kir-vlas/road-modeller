package com.drakezzz.roadmodeller.service.impl;

import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.repository.StatisticEntityRepository;
import com.drakezzz.roadmodeller.service.StatisticService;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final StatisticEntityRepository statisticEntityRepository;

    public StatisticServiceImpl(StatisticEntityRepository statisticEntityRepository) {
        this.statisticEntityRepository = statisticEntityRepository;
    }

    @Override
    public void collectStatistic(ModelState modelState) {
        StatisticEntity statisticEntity = getStatistic(modelState.getUuid());
        statisticEntity.getDrivers().addAll(modelState
                .getDrivers()
                .stream()
                .map(Driver::getId)
                .collect(Collectors.toSet())
        );
        statisticEntityRepository.save(statisticEntity);
    }

    @Override
    public StatisticEntity getStatistic(String modelId) {
        return statisticEntityRepository
                .findById(modelId)
                .orElseGet(() -> getEmpty(modelId));
    }

    private StatisticEntity getEmpty(String id) {
        StatisticEntity statisticEntity = new StatisticEntity();
        statisticEntity.setDrivers(new HashSet<>());
        statisticEntity.setId(id);
        return statisticEntity;
    }

}
