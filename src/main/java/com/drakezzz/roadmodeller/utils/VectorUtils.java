package com.drakezzz.roadmodeller.utils;

import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import lombok.experimental.UtilityClass;
import org.springframework.data.geo.Point;

import java.util.Optional;

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

    public static Optional<Point> calculateIntersectionPoint(RoadLane lane1, RoadLane lane2) {
        Point road1Coords1 = lane1.getCoordinates().get(0);
        Point road1Coords2 = lane1.getCoordinates().get(1);
        Point road2Coords1 = lane2.getCoordinates().get(0);
        Point road2Coords2 = lane2.getCoordinates().get(1);
        if (road1Coords1.getX() == road1Coords2.getX() ||
                road2Coords1.getX() == road2Coords2.getX()) {
            return calculateIntersectionForNotLinear(lane1, lane2);
        }
        double[] line1Func = calculateLinearCoords(road1Coords1, road1Coords2);
        double[] line2Func = calculateLinearCoords(road2Coords1, road2Coords2);

        double k1 = line1Func[0];
        double b1 = line1Func[1];

        double k2 = line2Func[0];
        double b2 = line2Func[1];

        if (k1 == k2) {
            return Optional.empty();
        }

        double x = (b2 - b1) / (k1 - k2);
        double y = k1 * x + b1;

        Point point = new Point(x, y);
        return Optional.of(point);
    }

    public static Optional<Point> calculateIntersectionForNotLinear(RoadLane lane1, RoadLane lane2) {
        Point road1Coords1 = lane1.getCoordinates().get(0);
        Point road1Coords2 = lane1.getCoordinates().get(1);
        Point road2Coords1 = lane2.getCoordinates().get(0);
        Point road2Coords2 = lane2.getCoordinates().get(1);
        if (road1Coords1.getX() == road1Coords2.getX() &&
                road2Coords1.getX() == road2Coords2.getX()) {
            return Optional.empty();
        }
        double x = 0;
        double[] lineFunc = null;
        if (road1Coords1.getX() == road1Coords2.getX()) {
            x = road1Coords1.getX();
            lineFunc = calculateLinearCoords(road2Coords1, road2Coords2);
        }
        if (road2Coords1.getX() == road2Coords2.getX()) {
            x = road2Coords1.getX();
            lineFunc = calculateLinearCoords(road1Coords1, road1Coords2);
        }

        if (lineFunc == null) {
            return Optional.empty();
        }

        double k = lineFunc[0];
        double b = lineFunc[1];
        double y = k * x + b;
        Point point = new Point(x,y);
        return Optional.of(point);
    }

    public double[] calculateLinearCoords(Point point1, Point point2) {
        double xCoeff = (point2.getY() - point1.getY())/(point2.getX() - point1.getX());
        double bCoeff = (point2.getX()*point1.getY() - point1.getX()*point2.getY())/(point2.getX() - point1.getX());
        return new double[]{xCoeff, bCoeff};
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
