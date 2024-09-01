package com.skyapi.weatherforecast.base;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.location.LocationApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;

@RestController
public class MainController {

	@GetMapping("/")
	public ResponseEntity<RootEntity> handleBaseURI() {
		return ResponseEntity.ok(createRootEntity());
	}
	
	private RootEntity createRootEntity() {
		RootEntity entity = new RootEntity();
		
		String locationsUrl = linkTo(methodOn(LocationApiController.class).listLocations()).toString();
		entity.setLocationsUrl(locationsUrl);
		
		String locationByCodeUrl = linkTo(methodOn(LocationApiController.class).getLocation(null)).toString();
		entity.setLocationByCodeUrl(locationByCodeUrl);
		
		String realtimeWeatherByIpUrl = linkTo(
				methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).toString();
		entity.setRealtimeWeatherByIpUrl(realtimeWeatherByIpUrl);
		
		String realtimeWeatherByCodeUrl = linkTo(
				methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(null)).toString();
		entity.setRealtimeWeatherByCodeUrl(realtimeWeatherByCodeUrl);	
		
		String hourlyForecastByIpUrl = linkTo(
				methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null)).toString();
		entity.setHourlyForecastByIpUrl(hourlyForecastByIpUrl);
		
		String hourlyForecastByCodeUrl = linkTo(
				methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(null, null)).toString();
		entity.setHourlyForecastByCodeUrl(hourlyForecastByCodeUrl);
		
		String dailyForecastByIpUrl = linkTo(
				methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).toString();
		entity.setDailyForecastByIpUrl(dailyForecastByIpUrl);		
		
		String dailyForecastByCodeUrl = linkTo(
				methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(null)).toString();
		entity.setDailyForecastByCodeUrl(dailyForecastByCodeUrl);
		
		String fullWeatherByIpUrl = linkTo(
				methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).toString();
		entity.setFullWeatherByIpUrl(fullWeatherByIpUrl);
		
		String fullWeatherByCodeUrl = linkTo(
				methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(null)).toString();
		entity.setFullWeatherByCodeUrl(fullWeatherByCodeUrl);		
		
		return entity;
	}
}