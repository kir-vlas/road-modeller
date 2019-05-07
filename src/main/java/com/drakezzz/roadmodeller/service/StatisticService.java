package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.StatisticEntity;

public interface StatisticService {

    void collectStatistic(ModelState modelState);

    StatisticEntity getStatistic(String modelId);

}
