package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;

public interface ModelRepositoryProvider {

    void saveToDatabase(ModelState modelState);

    ModelState getModelState(String id);

}
