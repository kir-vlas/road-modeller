package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
public class MockActionExecutor implements ContiniusActionExecutor {

    @Override
    public String initAction() {
        return null;
    }

    @Override
    public ModelState executeAction(String actionId) {
        ModelState modelState = new ModelState();
        UUID uuid = UUID.randomUUID();
        modelState.setUuid(uuid.toString());
        return modelState;
    }
}

