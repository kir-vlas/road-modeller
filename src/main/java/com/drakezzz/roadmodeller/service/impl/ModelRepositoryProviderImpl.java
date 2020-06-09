package com.drakezzz.roadmodeller.service.impl;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.repository.ModelStateRepository;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ModelRepositoryProviderImpl implements ModelRepositoryProvider {

    private final ModelStateRepository modelStateRepository;

    public ModelRepositoryProviderImpl(ModelStateRepository modelStateRepository) {
        this.modelStateRepository = modelStateRepository;
    }

    @Override
    public Mono<ModelState> saveToDatabase(ModelState modelState) {
        return modelStateRepository.save(modelState);
    }

    @Override
    public Mono<ModelState> getModelState(String id) {
        return modelStateRepository
                .findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new));
    }

    @Override
    public Flux<ModelId> getUnfinishedModels() {
        return modelStateRepository.findModelsIdByStatus()
                .map(ModelId::ofModelState);
    }

    @Override
    public Mono<Void> removeModel(String id) {
        return modelStateRepository.deleteById(id);
    }
}
