package com.drakezzz.roadmodeller.persistence.repository;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ModelStateRepository extends ReactiveMongoRepository<ModelState, String> {

    @Query( value = "{isCompleted: false}", fields = "{_id:1}")
    Flux<ModelState> findModelsIdByStatus();

}
