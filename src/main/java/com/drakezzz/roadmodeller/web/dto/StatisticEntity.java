package com.drakezzz.roadmodeller.web.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
import java.util.Set;

@Data
public class StatisticEntity {

    @Id
    private String id;

    private BigInteger overallCarsCount;

    private Set<String> drivers;

}
