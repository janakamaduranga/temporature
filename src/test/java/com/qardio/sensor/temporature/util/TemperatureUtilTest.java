package com.qardio.sensor.temporature.util;

import static org.junit.jupiter.api.Assertions.*;

import com.qardio.sensor.temporature.exception.TemperatureException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;

class TemperatureUtilTest {

    @Test
    void convertToInstantWhenValidStringThenSuccess() {
        final String DATE_STRING = "2017-02-10T09:59:59.99999Z";
        Instant instant = TemperatureUtil.convertToInstant(DATE_STRING);

        int year = instant.atZone(ZoneOffset.UTC).getYear();
        int month = instant.atZone(ZoneOffset.UTC).getMonthValue();
        int date = instant.atZone(ZoneOffset.UTC).getDayOfMonth();
        int hour = instant.atZone(ZoneOffset.UTC).getHour();
        int minute = instant.atZone(ZoneOffset.UTC).getMinute();
        int second = instant.atZone(ZoneOffset.UTC).getSecond();
        int nano = instant.atZone(ZoneOffset.UTC).getNano();

        assertEquals(2017,year);
        assertEquals(02, month);
        assertEquals(10, date);
        assertEquals(9, hour);
        assertEquals(59, minute);
        assertEquals(59, second);
        assertEquals(999990000, nano);
    }

    @Test
    void convertToInstantWhenInvalidStringThenException() {
        final String DATE_STRING = "2017-02-10T09:59:99999Z";

        TemperatureException temperatureException = assertThrows(TemperatureException.class,
                () -> TemperatureUtil.convertToInstant(DATE_STRING));

        assertEquals(TemperatureException.INVALID_DATE_FORMAT, temperatureException.getErrorCode());
    }
}
