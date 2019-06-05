package com.drakezzz.roadmodeller.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusResult {

    private String result;

    public static StatusResult ok() {
        return new StatusResult("ok");
    }

}
