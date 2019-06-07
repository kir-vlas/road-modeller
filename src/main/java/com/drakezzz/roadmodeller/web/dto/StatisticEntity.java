package com.drakezzz.roadmodeller.web.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

@Data
public class StatisticEntity {

    @Id
    private String id;

    private BigInteger overallCarsCount;

    private BigDecimal averageWaitingCars;

    private Set<String> drivers;

}
