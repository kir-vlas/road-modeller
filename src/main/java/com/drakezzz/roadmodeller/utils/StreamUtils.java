package com.drakezzz.roadmodeller.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Predicate;

@UtilityClass
public class StreamUtils {

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

}
