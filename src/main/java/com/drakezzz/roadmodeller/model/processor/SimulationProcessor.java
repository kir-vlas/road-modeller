package com.drakezzz.roadmodeller.model.processor;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;

public interface SimulationProcessor {

    ModelState simulate(ModelState modelState);

}
