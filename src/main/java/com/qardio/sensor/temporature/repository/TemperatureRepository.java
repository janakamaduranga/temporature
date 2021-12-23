package com.qardio.sensor.temporature.repository;

import com.qardio.sensor.temporature.model.TemperatureReading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.time.Instant;

@Repository
public interface TemperatureRepository extends ReactiveMongoRepository<TemperatureReading, BigInteger> {
    @Query("{'date' : { $gte: ?0, $lte: ?1 } }")
    Flux<TemperatureReading> findByDateBetween(Instant start, Instant end, Pageable pageable);
}
