package com.drakezzz.roadmodeller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    public ObjectMapper objectMapper() {
        //todo configure
        return new ObjectMapper();
    }

}
