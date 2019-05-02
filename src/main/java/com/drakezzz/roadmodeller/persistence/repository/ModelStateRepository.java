package com.drakezzz.roadmodeller.persistence.repository;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModelStateRepository extends MongoRepository<ModelState, String> {
}
