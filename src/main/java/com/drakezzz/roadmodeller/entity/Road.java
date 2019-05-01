package com.drakezzz.roadmodeller.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

import java.time.Duration;
import java.util.List;

@Data
public class Road {

    private int lanes;

    private Point origin;

    private Point end;

    private List<Car> cars;

    private Duration waitingTime;

    private double intensity;

}
