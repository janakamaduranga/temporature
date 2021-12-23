package com.qardio.sensor.temporature.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.qardio.sensor.temporature.util.CustomJsonInstantDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TemperatureDto implements Serializable {

    @JsonDeserialize(using = CustomJsonInstantDeserializer.class)
    @NotNull(message = "date is required")
    private Instant date ;

    @NotNull(message = "temperature is required")
    private Double temperature;
}
