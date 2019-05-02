package com.drakezzz.roadmodeller.web.dto;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.persistence.entity.Driver;
import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ModelSettings {

    private Set<Driver> drivers;

    private List<RoadLane> network;

    private List<Attribute> attributes;

    private BigDecimal maxDuration;

    private BigDecimal timeDelta;

    private Boolean isNeedGenerate;

}
