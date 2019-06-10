package com.drakezzz.roadmodeller.persistence.entity;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.geo.Point;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static com.drakezzz.roadmodeller.utils.VectorUtils.*;

@Data
public class Driver {

    @JsonIgnore
    private String id;

    @JsonIgnore
    private Car car;

    @JsonProperty("crd")
    private Point currentCoordinates;

    @JsonIgnore
    private Point originCoordinates;

    @JsonIgnore
    private Point destinationCoordinates;

    @JsonIgnore
    private List<Attribute> attributeList;

    @JsonIgnore
    private RoadLane currentRoad;

    @JsonIgnore
    private Boolean isFinished = false;

    @JsonIgnore
    private Boolean isWaitingGreenLight = false;

    @SuppressWarnings("unchecked")
    public <T> T getAttributeValue(Attribute attribute) {
        Assert.notNull(attribute, "Attribute must be present");

        return (T) attributeList.stream()
                .filter(attribute1 ->
                        attribute1.getName().equals(attribute.getName()))
                .findFirst()
                .map(Attribute::getValue)
                .orElse(attribute);
    }

    @SuppressWarnings("unchecked")
    public <T> void setAttribute(Attribute attribute, T value) {
        Assert.notNull(attribute, "Attribute must be present");

        attributeList.removeIf(attribute1 ->
                attribute1.getName().equals(attribute.getName()));
        attribute.setValue(value);
        attributeList.add(attribute);
    }

    public double getSafeSpeed(double distance, double safeTimeHeadway) {
        double maxDeceleration = getCar().getDeceleration();
        return (distance - 0.5 * maxDeceleration * Math.pow(safeTimeHeadway,2))/safeTimeHeadway;
    }

    public static void checkCollisions(Set<Driver> drivers) {
        for (Driver driver1 : drivers) {
            drivers.forEach(driver2 -> {
                if (driver1.equals(driver2)) {
                    return;
                }
                Point speedVector1 = getDestinationVector(driver1);
                Point speedVector2 = getDestinationVector(driver2);
                if (speedVector1.equals(speedVector2) && distance(driver1.getCurrentCoordinates(), driver2.getCurrentCoordinates()) < 10) {
                    driver1.setIsFinished(true);
                }
            });
        }
    }

    public static boolean isFinished(Driver driver) {
        Point currentCoordinates = driver.getCurrentCoordinates();
        Point destCoordinates = driver.getDestinationCoordinates();
        return distance(currentCoordinates, destCoordinates) < 5;
    }

    public static Point getDestinationVector(Driver driver) {
        return calculateSpeed(driver, 1);
    }

    public static Point calculateSpeed(Driver driver, double carSpeed) {
        Point currentCoordinates = driver.getCurrentCoordinates();
        Point destCoordinates = driver.getDestinationCoordinates();
        if (currentCoordinates.getX() != destCoordinates.getX()) {
            if (isXLower(currentCoordinates, destCoordinates)) {
                return new Point(carSpeed, 0);
            } else {
                return new Point(-carSpeed, 0);
            }
        }
        else {
            if (isYLower(currentCoordinates, destCoordinates)) {
                return new Point(0, carSpeed);
            } else {
                return new Point(0, -carSpeed);
            }
        }
    }

}
