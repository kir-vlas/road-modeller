package com.drakezzz.roadmodeller.persistence.repository;

import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticEntityRepository extends MongoRepository<StatisticEntity, String> {
}
