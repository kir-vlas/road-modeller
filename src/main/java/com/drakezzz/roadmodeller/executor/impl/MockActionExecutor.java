package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
//@Primary
public class MockActionExecutor implements ContiniusActionExecutor {

    @Override
    public String initAction(ModelSettings settings) {
        return UUID.randomUUID().toString();
    }

    @Override
    public ModelState executeAction(String actionId) {
        ModelState modelState = new ModelState();
        modelState.setUuid(actionId);
        modelState.setStep(RandomUtils.nextInt(0, 1000));
        return modelState;
    }
}
