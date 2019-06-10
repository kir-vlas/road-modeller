package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.web.dto.StatisticEntity;

public interface StatisticService {

    StatisticEntity getFullStatistic(String modelId);

    StatisticEntity getShortStatistic(String modelId);

}
