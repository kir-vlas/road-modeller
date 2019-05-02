package com.drakezzz.roadmodeller.model.impl;

import com.drakezzz.roadmodeller.model.DriverModel;
import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.drakezzz.roadmodeller.persistence.entity.Driver;
import org.springframework.stereotype.Service;

@Service
public class IntelligentDriverModel implements DriverModel {

    public static final Attribute<Double> V0 = new Attribute<>("IDM_Desired_Speed", 0.0);

    public static final Attribute<Double> T = new Attribute<>("Driver_Safe_Time_Headway", 1.2);

    public static final Attribute<Double> A = new Attribute<>("IDM_Acceleration", 1.25);

    public static final Attribute<Double> B = new Attribute<>("IDM_Deceleration", 2.09);

    public static final Attribute<Double> S0 = new Attribute<>("IDM_Stopping_Distance", 3.0);

    public static final Attribute<Double> F_SPEED = new Attribute<>("Driver_Speed_Limit_Adherence_Factor", 1.0);

    public static final Attribute<Integer> DELTA = new Attribute<>("Acceleration_Exponent", 4);

    @Override
    public double acceleration(Driver driver, double distanceToBlockingObject, double blockingObjectSpeed) {
        return acceleration(driver.getAttributeValue(S0),
                updateDesiredVelocity(driver),
                driver.getCar().getSpeed(),
                driver.getAttributeValue(DELTA),
                driver.getAttributeValue(T),
                driver.getCar().getSpeed() - blockingObjectSpeed,
                distanceToBlockingObject,
                driver.getAttributeValue(A),
                driver.getAttributeValue(B));
    }

    @Override
    public double updateDesiredVelocity(Driver driver) {
        double vMax = driver.getCar().getMaxSpeed();
        double laneSpeedLimit = driver.getCar().getCurrentLane().getLegalMaxSpeedInMetersPerSec();
        double desiredVelocity = desiredVelocity(vMax, laneSpeedLimit, driver.getAttributeValue(F_SPEED));
        driver.setAttribute(V0, desiredVelocity);
        return driver.getAttributeValue(V0);
    }

    private double acceleration(double s0, double v0, double v, int delta, double t, double deltaV, double s, double a, double b) {
        return desiredAcceleration(a, v, v0, delta) - a * Math.pow(desiredDynamicDistance(s0, v, t, deltaV, a, b)/s,2);
    }

    private double desiredAcceleration(double a, double v, double v0, int delta) {
        return a * (1-Math.pow(v/v0, delta));
    }

    private double desiredDynamicDistance(double s0, double v, double t, double deltaV, double a, double b) {
        return s0 + Math.max(0, v*t + (v * deltaV)/(2 * Math.sqrt(a*b)));
    }

    private double desiredVelocity(double maxSpeed, double speedLimit, double speedLimitAdeherenceFactor) {
        return Math.min(maxSpeed, speedLimitAdeherenceFactor * speedLimit);
    }
}
