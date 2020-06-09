package com.drakezzz.roadmodeller.executor.impl;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.SettingsUpdate;
import com.drakezzz.roadmodeller.web.dto.StatusResult;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Action executor for testing WebSocket transport system
 */
@Service
//@Primary
public class MockActionExecutor implements ContiniusActionExecutor {

    @Override
    public Mono<String> initAction(ModelSettings settings) {
        return Mono.just(UUID.randomUUID().toString());
    }

    @Override
    public Mono<ModelState> executeAction(String actionId) {
        ModelState modelState = new ModelState();
        modelState.setUuid(actionId);
        modelState.setStep(RandomUtils.nextInt(0, 1000));
        return Mono.just(modelState);
    }

    @Override
    public Mono<StatusResult> updateModel(String modelId, SettingsUpdate settingsUpdate){
        return Mono.just(StatusResult.ok());
    }
}

