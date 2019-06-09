package com.drakezzz.roadmodeller.executor;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import com.drakezzz.roadmodeller.web.dto.SettingsUpdate;
import com.drakezzz.roadmodeller.web.dto.StatusResult;

public interface ContiniusActionExecutor {

    String initAction(ModelSettings modelSettings);

    ModelState executeAction(String actionId);

    StatusResult updateModel(String modelId, SettingsUpdate settingsUpdate);

}
