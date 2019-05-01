package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.service.ModelRepositoryProvider;
import org.springframework.stereotype.Service;

@Service
public class ModellerExecutor implements ContiniusActionExecutor {

    private final ModelRepositoryProvider modelRepositoryProvider;

    public ModellerExecutor(ModelRepositoryProvider modelRepositoryProvider) {
        this.modelRepositoryProvider = modelRepositoryProvider;
    }

    @Override
    public String initAction() {
        return null;
    }

    @Override
    public ModelState executeAction(String actionId) {
        ModelState modelState = modelRepositoryProvider.getModelState(actionId);
        return null;
    }

}
