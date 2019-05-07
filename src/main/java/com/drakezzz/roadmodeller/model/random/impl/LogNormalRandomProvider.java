package com.drakezzz.roadmodeller.model.random.impl;

import com.drakezzz.roadmodeller.model.random.RandomProvider;
import org.springframework.stereotype.Service;

import static com.drakezzz.roadmodeller.utils.VectorUtils.sqr;

@Service
public class LogNormalRandomProvider implements RandomProvider {


    @Override
    public Object getRandomNumber(double distribution, double deviation) {
        double mu = Math.log(sqr(distribution) / Math.sqrt(sqr(deviation) + sqr(distribution)));
        double sigma = Math.sqrt(Math.log((sqr(deviation) / sqr(distribution)) + 1));
        return Math.exp(mu + RANDOM.nextDouble() * sigma);
    }


}
