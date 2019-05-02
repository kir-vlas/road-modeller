package com.drakezzz.roadmodeller.executor;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;

public interface ContiniusActionExecutor {

    String initAction(ModelSettings modelSettings);

    ModelState executeAction(String actionId);

}
