package com.drakezzz.roadmodeller.executor;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;

public interface ContiniusActionExecutor {

    String initAction();

    ModelState executeAction(String actionId);

}
