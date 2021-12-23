package com.qardio.sensor.temporature.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = {"com.qardio.sensor.temporature.repository"})
public class MongoConfig {
}
