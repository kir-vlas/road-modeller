package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

import java.time.Duration;

@Data
public class TrafficLight {

    private LightStatus status;

    private Duration redDelay;

    private Duration greenDelay;

    private Point coordinates;

}
