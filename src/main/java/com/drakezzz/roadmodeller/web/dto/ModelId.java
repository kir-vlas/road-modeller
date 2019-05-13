package com.drakezzz.roadmodeller.web.dto;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelId {

    private String id;

    public static ModelId ofModelState(ModelState modelState) {
        return new ModelId(modelState.getUuid());
    }

}
