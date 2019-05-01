package com.drakezzz.roadmodeller.entity;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class Pedestrian {

    private double speed;

    private boolean isOnCrosswalk;

    private Point coordinates;

    private boolean isWaitingGreenLight;

}
