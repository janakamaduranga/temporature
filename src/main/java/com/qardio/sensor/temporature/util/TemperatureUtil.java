package com.qardio.sensor.temporature.util;

import com.qardio.sensor.temporature.exception.TemperatureException;

import java.time.Instant;

public class TemperatureUtil {

        private TemperatureUtil() {

        }

        public static Instant convertToInstant(String date) {
            try {
                return Instant.parse(date);
            } catch (Exception e) {
                throw new TemperatureException(e,
                        String.format("%s is in invalid format ", date)
                        , TemperatureException.INVALID_DATE_FORMAT);
            }

        }

}
