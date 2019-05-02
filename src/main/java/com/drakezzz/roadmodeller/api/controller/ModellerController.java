package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.web.dto.ModelId;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ModellerController {

    private final ContiniusActionExecutor executor;

    public ModellerController(ContiniusActionExecutor executor) {
        this.executor = executor;
    }

    @GetMapping("/init")
    public ModelId initModel() {
        ModelSettings modelSettings = new ModelSettings();
        modelSettings.setIsNeedGenerate(true);
        String id = executor.initAction(modelSettings);
        return new ModelId(id);
    }

}
