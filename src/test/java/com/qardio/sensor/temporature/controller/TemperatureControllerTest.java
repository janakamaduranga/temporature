package com.qardio.sensor.temporature.controller;

import com.qardio.sensor.temporature.dto.TemperatureDto;
import com.qardio.sensor.temporature.service.TemperatureService;
import com.qardio.sensor.temporature.util.Range;
import com.qardio.sensor.temporature.util.TemperatureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TemperatureController.class)
class TemperatureControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    TemperatureService temperatureService;

    @Captor
    ArgumentCaptor<TemperatureDto> temperatureDtoArgumentCaptor;

    @Captor
    ArgumentCaptor<Instant> instantArgumentCaptor;

    @Captor
    ArgumentCaptor<Range> rangeArgumentCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;

    TemperatureDto temperatureDto = null;
    final double temperature = 12.33;
    final double temperature2 = 11.33;
    final String DATE_STRING = "2017-02-10T09:59:59.99999Z";
    final String DATE_STRING2 = "2017-02-10T09:00:59.99999Z";

    TemperatureDto temperatureDto2 = null;

    @BeforeEach
    public void setUp() {
        temperatureDto = new TemperatureDto();
        temperatureDto.setTemperature(temperature);
        temperatureDto.setDate(TemperatureUtil.convertToInstant(DATE_STRING));

        temperatureDto2 = new TemperatureDto();
        temperatureDto2.setTemperature(temperature2);
        temperatureDto2.setDate(TemperatureUtil.convertToInstant(DATE_STRING2));
    }

    @Test
    void saveWhenValidInputThenSuccess() {
        when(temperatureService.save(any(TemperatureDto.class))).thenReturn(Mono.just(temperatureDto));
        webTestClient.post()
                .uri("/temperatures")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(temperatureDto), TemperatureDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TemperatureDto.class)
                .value(temperatureFromDb -> {
                    assertEquals(temperatureDto.getTemperature(), temperatureFromDb.getTemperature());
                    assertEquals(temperatureDto.getDate(), temperatureFromDb.getDate());
                });
        verify(temperatureService).save(temperatureDtoArgumentCaptor.capture());
        assertEquals(temperatureDto.getTemperature(), temperatureDtoArgumentCaptor.getValue().getTemperature());
    }

    @Test
    void saveWhenInValidInputThenBadRequest() {
        temperatureDto.setTemperature(null);
        when(temperatureService.save(any(TemperatureDto.class))).thenReturn(Mono.just(temperatureDto));
        webTestClient.post()
                .uri("/temperatures")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(temperatureDto), TemperatureDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void saveWhenValidInputAndUnexpectedErrorThenInternalError() {
        when(temperatureService.save(any(TemperatureDto.class))).thenThrow(new RuntimeException());
        webTestClient.post()
                .uri("/temperatures")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(temperatureDto), TemperatureDto.class)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void saveBulkWhenValidInputThenSuccess() {
        when(temperatureService.saveAll(anyList())).thenReturn(Flux.just(temperatureDto, temperatureDto2));
        Flux<TemperatureDto> result = webTestClient.post()
                .uri("/temperatures/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.just(temperatureDto, temperatureDto2), TemperatureDto.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(TemperatureDto.class).getResponseBody()
                .log();

        StepVerifier.create(result)
                .expectSubscription()
                .expectNextMatches(temperatureDtoFromDb -> temperatureDtoFromDb.getTemperature()
                        .equals(temperatureDto.getTemperature()))
                .expectNextMatches(temperatureDtoFromDb -> temperatureDtoFromDb.getTemperature()
                        .equals(temperatureDto2.getTemperature()))
                .verifyComplete();

        verify(temperatureService).saveAll(anyList());
    }

    @Test
    void saveBulkWhenInValidInputThenBadRequest() {
        temperatureDto.setTemperature(null);
        when(temperatureService.saveAll(anyList())).thenReturn(Flux.just(temperatureDto, temperatureDto2));
        webTestClient.post()
                .uri("/temperatures/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.just(temperatureDto, temperatureDto2), TemperatureDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void saveBulkWhenValidInputAndUnexpectedErrorThenInternalError() {
        when(temperatureService.saveAll(anyList())).thenThrow(new RuntimeException());
        webTestClient.post()
                .uri("/temperatures/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.just(temperatureDto, temperatureDto2), TemperatureDto.class)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getTemperatureWhenValidInputAndDefaultParamThenSuccess() {
        when(temperatureService.findByRange(any(Instant.class), any(Range.class), any(Pageable.class)))
                .thenReturn(Flux.just(temperatureDto, temperatureDto2));

        Flux<TemperatureDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/temperatures")
                        .queryParam("date", DATE_STRING)
                        .queryParam("range", Range.HOUR.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(TemperatureDto.class).getResponseBody()
                .log();

        StepVerifier.create(result)
                .expectSubscription()
                .expectNextMatches(temperatureDtoFromDb -> temperatureDtoFromDb.getTemperature()
                        .equals(temperatureDto.getTemperature()))
                .expectNextMatches(temperatureDtoFromDb -> temperatureDtoFromDb.getTemperature()
                        .equals(temperatureDto2.getTemperature()))
                .verifyComplete();

        verify(temperatureService).findByRange(instantArgumentCaptor.capture(), rangeArgumentCaptor.capture(),
                pageableArgumentCaptor.capture());

        assertEquals(TemperatureUtil.convertToInstant(DATE_STRING), instantArgumentCaptor.getValue());
        assertEquals(Range.HOUR, rangeArgumentCaptor.getValue());
        assertEquals(0, pageableArgumentCaptor.getValue().getPageNumber());
        assertEquals(10, pageableArgumentCaptor.getValue().getPageSize());
    }

    @Test
    void getTemperatureWhenValidInputAndPageAndSizeParamGivenThenSuccess() {
        when(temperatureService.findByRange(any(Instant.class), any(Range.class), any(Pageable.class)))
                .thenReturn(Flux.just(temperatureDto, temperatureDto2));

        Flux<TemperatureDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/temperatures")
                        .queryParam("date", DATE_STRING)
                        .queryParam("range", Range.HOUR.name())
                        .queryParam("page", 1)
                        .queryParam("size", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(TemperatureDto.class).getResponseBody()
                .log();

        StepVerifier.create(result)
                .expectSubscription()
                .expectNextMatches(temperatureDtoFromDb -> temperatureDtoFromDb.getTemperature()
                        .equals(temperatureDto.getTemperature()))
                .expectNextMatches(temperatureDtoFromDb -> temperatureDtoFromDb.getTemperature()
                        .equals(temperatureDto2.getTemperature()))
                .verifyComplete();

        verify(temperatureService).findByRange(instantArgumentCaptor.capture(), rangeArgumentCaptor.capture(),
                pageableArgumentCaptor.capture());

        assertEquals(TemperatureUtil.convertToInstant(DATE_STRING), instantArgumentCaptor.getValue());
        assertEquals(Range.HOUR, rangeArgumentCaptor.getValue());
        assertEquals(1, pageableArgumentCaptor.getValue().getPageNumber());
        assertEquals(5, pageableArgumentCaptor.getValue().getPageSize());
    }

    @Test
    void getTemperatureWhenValidInputAndInvalidPageAndSizeParamGivenThenSuccess() {
        when(temperatureService.findByRange(any(Instant.class), any(Range.class), any(Pageable.class)))
                .thenReturn(Flux.just(temperatureDto, temperatureDto2));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/temperatures")
                        .queryParam("date", DATE_STRING)
                        .queryParam("range", Range.HOUR.name())
                        .queryParam("page", -1)
                        .queryParam("size", -5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
