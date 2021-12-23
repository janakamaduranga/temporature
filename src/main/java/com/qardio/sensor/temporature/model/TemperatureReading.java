package com.qardio.sensor.temporature.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;
import java.time.Instant;

@Document("temperature_reading")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemperatureReading {
    @Id
    private BigInteger id;

    @Field("date")
    private Instant date = Instant.now();

    @Field("temperature")
    private Double temperature;
}
