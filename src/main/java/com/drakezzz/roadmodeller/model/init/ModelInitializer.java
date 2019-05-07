package com.drakezzz.roadmodeller.model.init;

import com.drakezzz.roadmodeller.persistence.entity.ModelState;
import com.drakezzz.roadmodeller.persistence.entity.RoadLane;
import com.drakezzz.roadmodeller.utils.VectorUtils;
import com.drakezzz.roadmodeller.web.dto.ModelSettings;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;

public interface ModelInitializer {

    ModelState initializeModel(ModelSettings settings);

    default RoadLane buildRoad(Point origin, Point dest) {
        RoadLane roadLane = new RoadLane();
        roadLane.setMaxSpeedLimit(60);
        List<Point> coordinates = new ArrayList<>();
        if (origin.getX() != dest.getX()) {
            roadLane.setHorizontal(true);
        } else {
            roadLane.setHorizontal(false);
        }
        coordinates.add(origin);
        coordinates.add(dest);
        roadLane.setCoordinates(coordinates);
        roadLane.setLength(VectorUtils.distance(origin, dest));
        roadLane.setCanTurnLeft(false);
        roadLane.setCanTurnRight(false);
        return roadLane;
    }

}
