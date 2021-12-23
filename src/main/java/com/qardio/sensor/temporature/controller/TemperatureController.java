package com.qardio.sensor.temporature.controller;

import com.qardio.sensor.temporature.dto.TemperatureDto;
import com.qardio.sensor.temporature.service.TemperatureService;
import com.qardio.sensor.temporature.util.Range;
import com.qardio.sensor.temporature.util.TemperatureUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * expose related api for
 * temperature save, bulk save and read
 */
@RestController
@Validated
public class TemperatureController {

    private TemperatureService temperatureService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @Operation(description = "provide all orders for the user"
            , requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody())
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description  = "Success"),
            @ApiResponse(responseCode = "500", description = "internal error"),
            @ApiResponse(responseCode = "400", description = "bad request")
    }
    )
    @PostMapping(path = "/temperatures", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TemperatureDto>> save(@Valid @RequestBody TemperatureDto temperatureDto) {
        return new ResponseEntity<>(temperatureService.save(temperatureDto),
                HttpStatus.CREATED);
    }

    @Operation(description = "provide all orders for the user"
            , requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody())
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description  = "Success"),
            @ApiResponse(responseCode = "500", description = "internal error"),
            @ApiResponse(responseCode = "400", description = "bad request")
    }
    )
    @PostMapping(path = "/temperatures/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<TemperatureDto>> save(@Valid @RequestBody List<TemperatureDto> temperatureDto) {
        return new ResponseEntity<>(temperatureService.saveAll(temperatureDto),
                HttpStatus.CREATED);
    }

    @Operation(description = "provide all orders for the user"
            , requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody())
    @Parameters(
            value = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, required = false
                            , description = "page to be loaded, start from zero"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, required = false
                            , description = "size of the page to be loaded"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, required = false
                            , description = "size of the page to be loaded"),
                    @Parameter(name = "date", in = ParameterIn.QUERY, required = true
                            , description = "date/hour for data loading, string should be an " +
                            "ISO_INSTANT, eg: 2017-02-10T09:59:59.99999Z"),
                    @Parameter(name = "range", in = ParameterIn.QUERY, required = false
                            , description = "HOUR or DATE for which the data is loaded")

            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description  = "Success"),
            @ApiResponse(responseCode = "500", description = "internal error"),
            @ApiResponse(responseCode = "400", description = "bad request")
    }
    )
    @GetMapping(path = "/temperatures")
    public ResponseEntity<Flux<TemperatureDto>> getTemperature(@RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
                                               @RequestParam(value = "size", defaultValue = "10") @Min(0) int size,
                                               @RequestParam(name = "date", required = true) String date,
                                               @RequestParam(name = "range", defaultValue = "HOUR") Range range) {
        return ResponseEntity.ok(temperatureService.findByRange(TemperatureUtil.convertToInstant(date), range
        , PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "date"))));

    }
}
