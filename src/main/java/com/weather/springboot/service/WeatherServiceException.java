package com.weather.springboot.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class WeatherServiceException extends RuntimeException {
    private final HttpStatusCode status;

    public WeatherServiceException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
