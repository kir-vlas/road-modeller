package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;

import java.time.Duration;

@Data
public class TrafficLight {

    private LightStatus status;

    private Duration redDelay;

    private Duration greenDelay;

}
