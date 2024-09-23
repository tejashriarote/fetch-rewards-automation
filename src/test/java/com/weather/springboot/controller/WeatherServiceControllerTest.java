package com.weather.springboot.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.springboot.OpenWeatherServicesApplication;
import com.weather.springboot.model.Root;
import com.weather.springboot.model.Zip;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OpenWeatherServicesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherServiceControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate; // Autowired instead of manual instantiation

    @Test
    public void testGetWeatherByLocation() {
        // Prepare the URL with query parameters
        String url = UriComponentsBuilder.fromUriString("/weather/location")
                .queryParam("city", "Charlotte")
                .queryParam("state", "NC")
                .queryParam("countryCode", "US")
                .toUriString();

        // Perform the GET request
        ResponseEntity<Root[]> response = restTemplate.getForEntity(url, Root[].class);

        // Assert the response status and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Add additional assertions for response content if needed
    }

    @Test
    public void testGetWeatherByZipcodeValid() {
        // Prepare the URL with a valid ZIP code
        String url = UriComponentsBuilder.fromUriString("/weather/zipCode")
                .queryParam("zipCode", "28262")
                .queryParam("countryCode", "US")
                .toUriString();

        // Perform the GET request
        ResponseEntity<Zip> response = restTemplate.getForEntity(url, Zip.class);

        // Assert the response status and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Add additional assertions for response content if needed
    }

    @Test
    public void testGetWeatherByZipcodeInvalid() {
        // Prepare the URL with an invalid ZIP code
        String url = UriComponentsBuilder.fromUriString("/weather/zipCode")
                .queryParam("zipCode", "1") // Assuming this is an invalid ZIP code
                .toUriString();

        // Perform the GET request
        ResponseEntity<Zip> response = restTemplate.getForEntity(url, Zip.class);

        // Assert the response status
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Add additional assertions if needed
    }

    @Test
    public void testGetWeatherByZipcodeNotFound() {
        String url = UriComponentsBuilder.fromUriString("/weather/zipCode")
                .queryParam("zipCode", "99999") // Assuming this is an invalid ZIP code
                .toUriString();

        ResponseEntity<Zip> response = restTemplate.getForEntity(url, Zip.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // Additional assertions can be added here
    }


    @Test
    public void testRetrieveWeatherLocation() throws JSONException, JsonProcessingException {
        // Build request entity
        HttpEntity<String> entity = new HttpEntity<>(null);

        // Perform the API request
        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?city=Charlotte&state=NC&countryCode=US"),
                HttpMethod.GET, entity, Root[].class);

        // Convert response to JSON
        String jsonString = convertObjectToJson(response.getBody());

        // Read expected JSON from file
        String expectedJson = readExpectedJsonFromFile("./json/weather/location/expectedWeatherCharlotteResponse.json");

        // Assert the expected and actual JSONs
        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }
    @Test
    public void testRetrieveWeatherLocationWithoutStateAndCountry() throws JSONException, JsonProcessingException {
        // Build request entity
        HttpEntity<String> entity = new HttpEntity<>(null);

        // Perform the API request
        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?city=Charlotte&countryCode=US"),
                HttpMethod.GET, entity, Root[].class);

        // Convert response to JSON
        String jsonString = convertObjectToJson(response.getBody());

        // Read expected JSON from file
        String expectedJson = readExpectedJsonFromFile("./json/weather/location/expectedWeatherForCharlotteWithoutState.json");

        // Assert the expected and actual JSONs
        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }
    @Test
    public void testRetrieveWeatherInvalidLocation() throws JSONException, JsonProcessingException {
        // Build request entity
        HttpEntity<String> entity = new HttpEntity<>(null);

        // Perform the API request with an invalid location
        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?city=InvalidCity&state=XX&countryCode=ZZ"),
                HttpMethod.GET, entity, Root[].class);

        // Convert response to JSON
        String jsonString = convertObjectToJson(response.getBody());

        // Read expected JSON from file (an empty array in this case)
        //String expectedJson = readExpectedJsonFromFile("./json/weather/location/expectedWeatherInvalidLocationResponse.json");
        String expectedJson="[]";
        // Assert the expected and actual JSONs (empty array expected)
        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }

    @Test
    public void testRetrieveWeatherOnlyStateAndCountry() throws JSONException, JsonProcessingException {
        // Build request entity
        HttpEntity<String> entity = new HttpEntity<>(null);

        // Perform the API request with an invalid location
        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?state=XX&countryCode=ZZ"),
                HttpMethod.GET, entity, Root[].class);

        // Convert response to JSON
        String jsonString = convertObjectToJson(response.getBody());

        // Read expected JSON from file (an empty array in this case)
        //String expectedJson = readExpectedJsonFromFile("./json/weather/location/expectedWeatherInvalidLocationResponse.json");
        String expectedJson="[]";
        // Assert the expected and actual JSONs (empty array expected)
        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private String readExpectedJsonFromFile(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + fileName, e);
        }
    }
}
