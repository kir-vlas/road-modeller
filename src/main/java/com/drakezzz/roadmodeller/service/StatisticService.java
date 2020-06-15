package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import reactor.core.publisher.Mono;

public interface StatisticService {

    Mono<StatisticEntity> getFullStatistic(String modelId);

    Mono<StatisticEntity> getShortStatistic(String modelId);

}
