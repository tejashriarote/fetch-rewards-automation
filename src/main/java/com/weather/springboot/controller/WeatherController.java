package com.weather.springboot.controller;

import com.weather.springboot.model.Root;
import com.weather.springboot.model.Zip;
import com.weather.springboot.service.WeatherService;
import com.weather.springboot.service.WeatherServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/location")
    public List<Root> getWeatherByLocation(@RequestParam List<String> locations) {

        StringBuilder locationBuilder = new StringBuilder();


        List<Root> results = new ArrayList<>();

        for (String location : locations) {
            String formattedLocation = location.replace("/", ",");
            var ans = weatherService.getWeatherByLocation(formattedLocation);
            if (ans.length > 0) {
                results.add(ans[0]);
            }

        }
        return results;
    }

    @GetMapping("/zipCode")
    public ResponseEntity<List<Zip>> getWeatherByZipcode(@RequestParam List<String> zipCodes) {


        List<Zip> zipWeatherData = new ArrayList<>();

        for (String zipCode : zipCodes) {
            StringBuilder location = new StringBuilder(zipCode);
            try {
                Zip zipData = weatherService.getWeatherByZipcode(location.toString());
                zipWeatherData.add(zipData);

            } catch (WeatherServiceException e) {
                // Return 404 if ZIP code is not found or any other error status you deem appropriate
                return ResponseEntity.status(e.getStatus()).body(null);
            }
        }

        return ResponseEntity.ok(zipWeatherData);


    }


}
