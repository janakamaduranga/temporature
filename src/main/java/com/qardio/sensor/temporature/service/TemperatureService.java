package com.qardio.sensor.temporature.service;

import com.qardio.sensor.temporature.dto.TemperatureDto;
import com.qardio.sensor.temporature.model.TemperatureReading;
import com.qardio.sensor.temporature.repository.TemperatureRepository;
import com.qardio.sensor.temporature.util.Range;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;


/**
 * expose service methods for
 * temperature manipulation
 */
@Service
public class TemperatureService {

    private TemperatureRepository temperatureRepository;
    private ModelMapper modelMapper;

    public TemperatureService(TemperatureRepository temperatureRepository,
                              ModelMapper modelMapper) {
        this.temperatureRepository = temperatureRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Mono<TemperatureDto> save(TemperatureDto temperatureDto) {
        TemperatureReading temperatureReading = modelMapper.map(temperatureDto, TemperatureReading.class);
        return temperatureRepository.save(temperatureReading).map(savedTemp ->
                        modelMapper.map(savedTemp, TemperatureDto.class)
                );
    }

    /**
     * save list of temperature dtos within a same transaction
     * @param temperatureDtoList
     * @return
     */
    @Transactional
    public Flux<TemperatureDto> saveAll(List<TemperatureDto> temperatureDtoList) {
        if(!CollectionUtils.isEmpty(temperatureDtoList)) {
            List<TemperatureReading> temperatureReadingDos = temperatureDtoList
                    .stream().map(temperatureDto -> modelMapper.map(temperatureDto, TemperatureReading.class))
                            .collect(Collectors.toList());
            return temperatureRepository.saveAll(temperatureReadingDos)
                    .map(temperatureReading -> modelMapper.map(temperatureReading, TemperatureDto.class));
        }
        return Flux.empty();
    }

    /**
     * perform search based on the range parameter given,
     * if it is the DATE, search within the day(day start from 00:00:00:00 and end in 23:59:59:999999999),
     * if it is the HOUR, search within the hour(hour start from hour:00:00:00 and end in hour:59:59:999999999).
     * @param date
     * @param range
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Flux<TemperatureDto> findByRange(Instant date,
                                            Range range,
                                            Pageable pageable) {
        Instant end;
        if(Range.DATE == range) {
            date = date.atZone(ZoneOffset.UTC)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant();
            end = date.atZone(ZoneOffset.UTC)
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(999999999)
                    .toInstant();

        } else {
            date = date.atZone(ZoneOffset.UTC)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant();
            end = date.atZone(ZoneOffset.UTC)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(999999999)
                    .toInstant();
        }
        return temperatureRepository.findByDateBetween(date, end, pageable)
                .map(temperatureReading -> modelMapper.map(temperatureReading, TemperatureDto.class));
    }
}
