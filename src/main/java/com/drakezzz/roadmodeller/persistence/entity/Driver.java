package com.drakezzz.roadmodeller.persistence.entity;

import com.drakezzz.roadmodeller.model.entity.Attribute;
import lombok.Data;
import org.springframework.data.geo.Point;
import org.springframework.util.Assert;

import java.util.List;

@Data
public class Driver {

    private String id;

    private Car car;

    private Point currentCoordinates;

    private Point originCoordinates;

    private Point destinationCoordinates;

    private List<Attribute> attributeList;

    private boolean isFinished;

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

}
