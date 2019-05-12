package com.drakezzz.roadmodeller.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {

    private double x;

    private double y;

    public Point toPoint() {
        return new Point(x, y);
    }

}
