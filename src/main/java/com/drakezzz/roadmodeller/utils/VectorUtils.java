package com.drakezzz.roadmodeller.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.geo.Point;

@UtilityClass
public class VectorUtils {

    public double distance(Point or, Point de) {
        return Math.sqrt(sqr(de.getX() - or.getX()) + sqr(de.getY() - or.getY()));
    }

    public static double sqr(double digit) {
        return Math.pow(digit, 2);
    }

}
