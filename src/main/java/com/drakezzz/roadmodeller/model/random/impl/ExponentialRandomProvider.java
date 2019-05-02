package com.drakezzz.roadmodeller.model.random.impl;

import com.drakezzz.roadmodeller.model.random.RandomProvider;
import org.springframework.stereotype.Service;

@Service
public class ExponentialRandomProvider implements RandomProvider {

    @Override
    public Double getRandomNumber(double distribution, double deviation) {
        return -Math.log(RANDOM.nextDouble()) * distribution;
    }
}
