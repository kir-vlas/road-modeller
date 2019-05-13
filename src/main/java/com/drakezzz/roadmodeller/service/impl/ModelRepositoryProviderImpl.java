package com.drakezzz.roadmodeller.service.impl;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.repository.ModelStateRepository;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import org.springframework.stereotype.Service;

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
    public void saveToDatabase(ModelState modelState) {
        modelStateRepository.save(modelState);
    }

    @Override
    public ModelState getModelState(String id) {
        return modelStateRepository
                .findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<ModelId> getUnfinishedModels() {
        return modelStateRepository.findModelsIdByStatus()
                .stream()
                .map(ModelId::ofModelState)
                .collect(Collectors.toList());
    }

    @Override
    public void removeModel(String id) {
        modelStateRepository.deleteById(id);
    }
}
