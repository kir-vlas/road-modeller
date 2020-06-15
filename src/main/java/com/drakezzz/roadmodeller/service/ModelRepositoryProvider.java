package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ModelRepositoryProvider {

    Mono<ModelState> saveToDatabase(ModelState modelState);

    Mono<ModelState> getModelState(String id);

    Flux<ModelId> getUnfinishedModels();

    Mono<Void> removeModel(String id);

}
