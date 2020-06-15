package com.drakezzz.roadmodeller.persistence.repository;

import com.drakezzz.roadmodeller.web.dto.StatisticEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StatisticEntityRepository extends ReactiveMongoRepository<StatisticEntity, String> {
}
