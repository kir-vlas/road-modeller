package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelId;

import java.util.List;

public interface ModelRepositoryProvider {

    void saveToDatabase(ModelState modelState);

    ModelState getModelState(String id);

    List<ModelId> getUnfinishedModels();

    void removeModel(String id);

}
