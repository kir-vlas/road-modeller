package com.drakezzz.roadmodeller.model.random;

import java.util.Random;

public interface RandomProvider <T> {

    Random RANDOM = new Random();

    T getRandomNumber(double distribution, double deviation);

}
