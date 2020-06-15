package com.drakezzz.roadmodeller.executor;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.SettingsUpdate;
import com.drakezzz.roadmodeller.web.dto.StatusResult;
import reactor.core.publisher.Mono;

public interface ContiniusActionExecutor {

    Mono<String> initAction(ModelSettings modelSettings);

    Mono<ModelState> executeAction(String actionId);

    Mono<StatusResult> updateModel(String modelId, SettingsUpdate settingsUpdate);

}
