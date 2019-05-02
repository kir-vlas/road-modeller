package com.drakezzz.roadmodeller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ModellerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModellerApplication.class, args);
    }

}
