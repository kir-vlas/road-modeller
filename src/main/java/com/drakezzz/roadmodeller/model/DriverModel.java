package com.drakezzz.roadmodeller.model;

import com.drakezzz.roadmodeller.persistence.entity.Driver;

public interface DriverModel {

    double acceleration(Driver driver, double distanceToBlockingObject, double blockingObjectSpeed);

    double updateDesiredVelocity(Driver driver);

}
