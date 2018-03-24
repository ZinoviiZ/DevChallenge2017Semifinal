package com.dev.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.WriteConcernResolver;

import static com.mongodb.WriteConcern.SAFE;

/**
 * MongoDb configuration.
 */
@Configuration
public class MongoConfig {

    /**
     * Bean which sets MongoDB in safety mod for multithreading.
     */
    @Bean
    public WriteConcernResolver writeConcernResolver() {
        return action -> SAFE;
    }
}
