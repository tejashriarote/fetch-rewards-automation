package com.weather.springboot.service;

import com.weather.springboot.model.Root;
import com.weather.springboot.model.Zip;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {
    private final RestTemplate restTemplate;

    @Value("${appKey}")
    private String appKey;

    @Value("${apiUrlByLocation}")
    private String apiUrlByLocation;

    @Value("${apiUrlByZipcode}")
    private String apiUrlByZipcode;

    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Root[] getWeatherByLocation(String location) {
        String url = buildUriWithParams(apiUrlByLocation, "q", location, "limit", "5");
        return getResponse(url, Root[].class).getBody();
    }

    public Zip getWeatherByZipcode(String zipLocation) {

        String url = buildUriWithParams(apiUrlByZipcode, "zip", zipLocation);
        try {
            ResponseEntity<Zip> response = getResponse(url, Zip.class);


            return response.getBody();
        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new WeatherServiceException("Invalid ZIP code provided", HttpStatus.BAD_REQUEST);
            }
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new WeatherServiceException("Zip Code Not Found", HttpStatus.NOT_FOUND);
            }

            throw new WeatherServiceException("Error fetching weather data", e.getStatusCode());
        }


    }


    private String buildUriWithParams(String baseUrl, String... queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        StringBuilder locationBuilder = new StringBuilder();

        for (int i = 0; i < queryParams.length; i += 2) {
            String paramValue = queryParams[i + 1] != null ? queryParams[i + 1] : "";
            if (!paramValue.isEmpty()) {
                locationBuilder.append(queryParams[i]).append("=").append(paramValue);
                builder.queryParam(queryParams[i], paramValue); // Add the query param to the URL builder
            }
        }
        builder.queryParam("appid", appKey); // Add API key

        return builder.toUriString();
    }
    
    private <T> ResponseEntity<T> getResponse(String url, Class<T> responseType) {
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
        return response;
    }
}
