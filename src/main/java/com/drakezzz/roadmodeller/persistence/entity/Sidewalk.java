package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.List;

@Data
public class Sidewalk {

    private Point origin;

    private Point destination;

    private double intensity;

    private List<Pedestrian> pedestrians;

}
