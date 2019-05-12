package com.drakezzz.roadmodeller.web.dto;

import com.drakezzz.roadmodeller.persistence.entity.LightStatus;
import com.drakezzz.roadmodeller.persistence.entity.TrafficLight;
import lombok.Data;

@Data
public class TrafficLightDto {

    private LightStatus status;

    private double redDelay;

    private double greenDelay;

    private PointDto coordinates;

    public TrafficLight toTrafficLight() {
        TrafficLight trafficLight = new TrafficLight();
        trafficLight.setStatus(status);
        trafficLight.setRedDelay(redDelay);
        trafficLight.setGreenDelay(greenDelay);
        trafficLight.setCoordinates(coordinates.toPoint());
        return trafficLight;
    }

}
