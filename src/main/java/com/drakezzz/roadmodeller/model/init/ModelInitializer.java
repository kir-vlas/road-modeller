package com.drakezzz.roadmodeller.model.init;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;

public interface ModelInitializer {

    ModelState initializeModel(ModelSettings settings);

}
