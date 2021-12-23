package com.qardio.sensor.temporature.advice;

import com.qardio.sensor.temporature.controller.TemperatureController;
import com.qardio.sensor.temporature.exception.TemperatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * center place to handle all the exceptions
 */
@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice(assignableTypes = {
        TemperatureController.class})
public class ControllerAdvice {

    private static final String ERROR = "Error:";

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleArgumentMismatchException(ServerWebInputException ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(WebExchangeBindException ex) {
        log.error(ERROR, ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return new ResponseEntity<>(processFieldErrors(fieldErrors), (HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TemperatureException.class)
    public ResponseEntity<Object> handleOrderException(TemperatureException ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), getStatusCode(ex.getErrorCode()));
    }

    private HttpStatus getStatusCode(int errorCode) {
        if(errorCode == TemperatureException.INVALID_DATE_FORMAT) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalError(Exception ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        StringBuilder errorBuilder = new StringBuilder();
        for (org.springframework.validation.FieldError fieldError : fieldErrors) {
            errorBuilder.append(fieldError.getField()).append(" : ")
                    .append(fieldError.getDefaultMessage());
        }
        return errorBuilder.toString();
    }
}
