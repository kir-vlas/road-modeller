package com.drakezzz.roadmodeller.persistence.repository;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ModelStateRepository extends MongoRepository<ModelState, String> {

    @Query( value = "{isCompleted: false}", fields = "{_id:1}")
    List<ModelState> findModelsIdByStatus();

}
