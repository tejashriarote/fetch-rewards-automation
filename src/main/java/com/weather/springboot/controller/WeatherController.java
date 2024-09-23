package com.weather.springboot.controller;

import com.weather.springboot.model.Root;
import com.weather.springboot.model.Zip;
import com.weather.springboot.service.WeatherService;
import com.weather.springboot.service.WeatherServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    @Autowired
    private WeatherService weatherService;
    //create interface for weatherService

    //build different api for zipcode

    @GetMapping("/location")
    public Root[] getWeatherByLocation(@RequestParam(required = false) String city,
                             @RequestParam(required = false) String state,
                             @RequestParam(required = false) String countryCode) {

        StringBuilder locationBuilder = new StringBuilder();

        // Add state if it's not null or empty

        if (city != null && !city.isEmpty()) {
            locationBuilder.append(city);
        }

// Add state if it's not null or empty, preceded by a comma if city was added
        if (state != null && !state.isEmpty()) {
            if (locationBuilder.length() > 0) {
                locationBuilder.append(",");
            }
            locationBuilder.append(state);
        }

// Add country code if it's not null or empty, preceded by a comma if any previous value was added
        if (countryCode != null && !countryCode.isEmpty()) {
            if (locationBuilder.length() > 0) {
                locationBuilder.append(",");
            }
            locationBuilder.append(countryCode);
        }

        String location = locationBuilder.toString();
        return weatherService.getWeatherByLocation(location);
    }

    @GetMapping("/zipCode")
    public ResponseEntity<Zip> getWeatherByZipcode(@RequestParam String zipCode,
                                              @RequestParam(required = false) String countryCode) {

        // Use StringBuilder to build the location string
        StringBuilder location = new StringBuilder(zipCode);

        if (countryCode != null && !countryCode.isEmpty()) {
            location.append(",").append(countryCode); // append countryCode if it exists
        }

        try {
            Zip zipData = weatherService.getWeatherByZipcode(location.toString());
            return ResponseEntity.ok(zipData);
        } catch (WeatherServiceException e) {
            // Return 404 if ZIP code is not found or any other error status you deem appropriate
            return ResponseEntity.status(e.getStatus()).body(null);
        }

        //return weatherService.getWeatherByZipcode(location.toString());
    }


}
