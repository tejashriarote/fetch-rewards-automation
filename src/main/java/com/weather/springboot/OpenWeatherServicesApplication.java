package com.weather.springboot;

import com.weather.springboot.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenWeatherServicesApplication {

	@Autowired
	private static WeatherService weatherService;

	public static void main(String[] args) {

		SpringApplication.run(OpenWeatherServicesApplication.class, args);



		/*String city = args[0];
		String state = args[1];
		String zipCode = args[2];


		// Combine city, state, and zipCode into a single location string
		String location = city + "," + state + "," + zipCode;
		Root[] result = weatherService.getWeather(location);
		for(Root r:result)
		{
			System.out.println(r.toString());
		}*/
	}
}
