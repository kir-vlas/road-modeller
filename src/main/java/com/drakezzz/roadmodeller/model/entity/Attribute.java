package com.drakezzz.roadmodeller.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attribute<T> {

    private String name;

    private T value;

}
