package com.drakezzz.roadmodeller.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.geo.Point;

@UtilityClass
public class VectorUtils {

    public static final Point ZERO = new Point(0, 0);

    public double distance(Point or, Point de) {
        return Math.sqrt(sqr(de.getX() - or.getX()) + sqr(de.getY() - or.getY()));
    }

    public static Point add(Point point1, Point point2) {
        return new Point(point1.getX() + point2.getX(), point1.getY() + point2.getY());
    }

    public static double sqr(double digit) {
        return Math.pow(digit, 2);
    }

    public static int compareCoordinates(Point point1, Point point2) {
        if (isYLower(point1, point2)) return -1;
        if (isYLarger(point1, point2)) return +1;
        if (isXLower(point1, point2)) return -1;
        if (isXLarger(point1, point2)) return +1;
        return 0;
    }

    public static boolean isXLarger(Point point1, Point point2) {
        return point1.getX() > point2.getX();
    }

    public static boolean isXLower(Point point1, Point point2) {
        return point1.getX() < point2.getX();
    }

    public static boolean isYLarger(Point point1, Point point2) {
        return point1.getY() > point2.getY();
    }

    public static boolean isYLower(Point point1, Point point2) {
        return point1.getY() < point2.getY();
    }

}
