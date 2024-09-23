package com.weather.springboot.controller;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OpenWeatherServicesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherServiceControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate; // Autowired instead of manual instantiation

    @Test
    public void testGetWeatherByLocation() {
        String url = UriComponentsBuilder.fromUriString("/weather/location")
                .queryParam("locations", "Charlotte/NC/US")
                .toUriString();

        ResponseEntity<List<Root>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Root>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetWeatherByZipcodeValid() {

        String url = UriComponentsBuilder.fromUriString("/weather/zipCode")
                .queryParam("zipCodes", "28262")
                .toUriString();


        ResponseEntity<List<Zip>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Zip>>() {
                }
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    public void testGetWeatherByZipcodeInvalid() {

        String url = UriComponentsBuilder.fromUriString("/weather/zipCode")
                .queryParam("zipCodes", "1") // Assuming this is an invalid ZIP code
                .toUriString();


        ResponseEntity<List<Zip>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Zip>>() {
                }
        );


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void testGetWeatherByZipcodeNotFound() {
        String url = UriComponentsBuilder.fromUriString("/weather/zipCode")
                .queryParam("zipCodes", "99999") // Assuming this is an invalid ZIP code
                .toUriString();

        ResponseEntity<List<Zip>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Zip>>() {
                }
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }


    @Test
    public void testRetrieveWeatherLocation() throws JSONException, JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(null);


        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?locations=Charlotte/NC/US"),
                HttpMethod.GET, entity, Root[].class);


        String jsonString = convertObjectToJson(response.getBody());


        String expectedJson = readExpectedJsonFromFile("./json/weather/location/expectedWeatherCharlotteResponse.json");


        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }

    @Test
    public void testRetrieveWeatherLocationWithoutStateAndCountry() throws JSONException, JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(null);


        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?locations=Charlotte/US"),
                HttpMethod.GET, entity, Root[].class);


        String jsonString = convertObjectToJson(response.getBody());


        String expectedJson = readExpectedJsonFromFile("./json/weather/location/expectedWeatherForCharlotteWithoutState.json");


        JSONAssert.assertEquals(expectedJson, jsonString, true);
    }

    @Test
    public void testRetrieveWeatherInvalidLocation() throws JSONException, JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(null);


        ResponseEntity<Root[]> response = restTemplate.exchange(
                createURLWithPort("/weather/location?locations=InvalidCity/XX/ZZ"),
                HttpMethod.GET, entity, Root[].class);


        String jsonString = convertObjectToJson(response.getBody());


        String expectedJson = "[]";

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
