package com.drakezzz.roadmodeller.model.random.impl;

import com.drakezzz.roadmodeller.model.random.RandomProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class GaussianRandomProvider implements RandomProvider {

    @Override
    public Object getRandomNumber(double distribution, double deviation) {
        return RANDOM.nextGaussian() * distribution + deviation;
    }

}
