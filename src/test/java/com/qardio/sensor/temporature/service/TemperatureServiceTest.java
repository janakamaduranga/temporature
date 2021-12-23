package com.qardio.sensor.temporature.service;

import com.qardio.sensor.temporature.dto.TemperatureDto;
import com.qardio.sensor.temporature.model.TemperatureReading;
import com.qardio.sensor.temporature.repository.TemperatureRepository;
import com.qardio.sensor.temporature.util.Range;
import com.qardio.sensor.temporature.util.TemperatureUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZoneOffset;

@ExtendWith(MockitoExtension.class)
class TemperatureServiceTest {

    @Mock
    private TemperatureRepository temperatureRepository;

    @Mock
    ModelMapper modelMapper;

    TemperatureService temperatureService = null;

    @Captor
    ArgumentCaptor<Instant> startDateCaptor;

    @Captor
    ArgumentCaptor<Instant> endDateCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    @BeforeEach
    public void setUp() {
        temperatureService = new TemperatureService(temperatureRepository, modelMapper);
    }

    @Test
    void findByRangeWhenRangeIsDATE() {
        final String DATE_STRING = "2017-02-10T09:59:59.99999Z";
        Instant dateArgument = TemperatureUtil.convertToInstant(DATE_STRING);

        final String DATE_STRING_SAVED = "2017-02-10T09:59:59.99Z";
        Instant dateSaved = TemperatureUtil.convertToInstant(DATE_STRING_SAVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "date"));

        TemperatureReading temperatureReading = new TemperatureReading();
        temperatureReading.setTemperature(23.23);
        temperatureReading.setDate(dateSaved);

        TemperatureDto temperatureDto = new TemperatureDto();
        temperatureDto.setTemperature(temperatureReading.getTemperature());
        temperatureDto.setDate(temperatureReading.getDate());

        when(temperatureRepository.findByDateBetween(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(Flux.just(temperatureReading));
        when(modelMapper.map(temperatureReading, TemperatureDto.class)).thenReturn(temperatureDto);

        StepVerifier
                .create(temperatureService.findByRange(dateArgument, Range.DATE, pageable))
                .consumeNextWith(temperatureDtoFromDb -> {
                    assertEquals(temperatureReading.getTemperature(), temperatureDtoFromDb.getTemperature());
                    assertEquals(temperatureReading.getDate(), temperatureDtoFromDb.getDate());
                })
                .verifyComplete();

        verify(temperatureRepository)
                .findByDateBetween(startDateCaptor.capture(), endDateCaptor.capture(), pageableCaptor.capture());

        Instant start = startDateCaptor.getValue();
        Instant end = endDateCaptor.getValue();

        assertEquals(pageable, pageableCaptor.getValue());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getHour());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getNano());

        assertEquals(23, end.atZone(ZoneOffset.UTC).getHour());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(999999999, end.atZone(ZoneOffset.UTC).getNano());

    }

    @Test
    void findByRangeWhenRangeIsDATEAndEmptyResult() {
        final String DATE_STRING = "2017-02-10T09:59:59.99999Z";
        Instant dateArgument = TemperatureUtil.convertToInstant(DATE_STRING);

        final String DATE_STRING_SAVED = "2017-02-10T09:59:59.99Z";
        Instant dateSaved = TemperatureUtil.convertToInstant(DATE_STRING_SAVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "date"));

        TemperatureReading temperatureReading = new TemperatureReading();
        temperatureReading.setTemperature(23.23);
        temperatureReading.setDate(dateSaved);

        TemperatureDto temperatureDto = new TemperatureDto();
        temperatureDto.setTemperature(temperatureReading.getTemperature());
        temperatureDto.setDate(temperatureReading.getDate());

        when(temperatureRepository.findByDateBetween(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(Flux.empty());

        StepVerifier
                .create(temperatureService.findByRange(dateArgument, Range.DATE, pageable))
                .verifyComplete();

        verify(temperatureRepository)
                .findByDateBetween(startDateCaptor.capture(), endDateCaptor.capture(), pageableCaptor.capture());

        Instant start = startDateCaptor.getValue();
        Instant end = endDateCaptor.getValue();

        assertEquals(pageable, pageableCaptor.getValue());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getHour());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getNano());

        assertEquals(23, end.atZone(ZoneOffset.UTC).getHour());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(999999999, end.atZone(ZoneOffset.UTC).getNano());

    }

    @Test
    void findByRangeWhenRangeIsHOUR() {
        final String DATE_STRING = "2017-02-10T09:44:00Z";
        Instant dateArgument = TemperatureUtil.convertToInstant(DATE_STRING);

        final String DATE_STRING_SAVED = "2017-02-10T09:59:59.99Z";
        Instant dateSaved = TemperatureUtil.convertToInstant(DATE_STRING_SAVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "date"));

        TemperatureReading temperatureReading = new TemperatureReading();
        temperatureReading.setTemperature(23.23);
        temperatureReading.setDate(dateSaved);

        TemperatureDto temperatureDto = new TemperatureDto();
        temperatureDto.setTemperature(temperatureReading.getTemperature());
        temperatureDto.setDate(temperatureReading.getDate());

        when(temperatureRepository.findByDateBetween(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(Flux.just(temperatureReading));
        when(modelMapper.map(temperatureReading, TemperatureDto.class)).thenReturn(temperatureDto);

        StepVerifier
                .create(temperatureService.findByRange(dateArgument, Range.HOUR, pageable))
                .consumeNextWith(temperatureDtoFromDb -> {
                    assertEquals(temperatureReading.getTemperature(), temperatureDtoFromDb.getTemperature());
                    assertEquals(temperatureReading.getDate(), temperatureDtoFromDb.getDate());
                })
                .verifyComplete();

        verify(temperatureRepository)
                .findByDateBetween(startDateCaptor.capture(), endDateCaptor.capture(), pageableCaptor.capture());

        Instant start = startDateCaptor.getValue();
        Instant end = endDateCaptor.getValue();

        assertEquals(pageable, pageableCaptor.getValue());
        assertEquals(9, start.atZone(ZoneOffset.UTC).getHour());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getNano());

        assertEquals(9, end.atZone(ZoneOffset.UTC).getHour());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(999999999, end.atZone(ZoneOffset.UTC).getNano());

    }

    @Test
    void findByRangeWhenRangeIsHOURAndEmptyResult() {
        final String DATE_STRING = "2017-02-10T09:55:00Z";
        Instant dateArgument = TemperatureUtil.convertToInstant(DATE_STRING);

        final String DATE_STRING_SAVED = "2017-02-10T09:59:59.99Z";
        Instant dateSaved = TemperatureUtil.convertToInstant(DATE_STRING_SAVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "date"));

        TemperatureReading temperatureReading = new TemperatureReading();
        temperatureReading.setTemperature(23.23);
        temperatureReading.setDate(dateSaved);

        TemperatureDto temperatureDto = new TemperatureDto();
        temperatureDto.setTemperature(temperatureReading.getTemperature());
        temperatureDto.setDate(temperatureReading.getDate());

        when(temperatureRepository.findByDateBetween(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(Flux.empty());

        StepVerifier
                .create(temperatureService.findByRange(dateArgument, Range.HOUR, pageable))
                .verifyComplete();

        verify(temperatureRepository)
                .findByDateBetween(startDateCaptor.capture(), endDateCaptor.capture(), pageableCaptor.capture());

        Instant start = startDateCaptor.getValue();
        Instant end = endDateCaptor.getValue();

        assertEquals(pageable, pageableCaptor.getValue());
        assertEquals(9, start.atZone(ZoneOffset.UTC).getHour());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(00, start.atZone(ZoneOffset.UTC).getNano());

        assertEquals(9, end.atZone(ZoneOffset.UTC).getHour());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(59, end.atZone(ZoneOffset.UTC).getSecond());
        assertEquals(999999999, end.atZone(ZoneOffset.UTC).getNano());

    }
}
