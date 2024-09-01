package com.skyapi.weatherforecast.daily;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/daily")
@Validated
public class DailyWeatherApiController {

	private DailyWeatherService dailyWeatherService;
	private GeolocationService locationService;
	private ModelMapper modelMapper;
	
	public DailyWeatherApiController(DailyWeatherService dailyWeatherService, GeolocationService locationService, ModelMapper modelMapper) {
		super();
		this.dailyWeatherService = dailyWeatherService;
		this.locationService = locationService;
		this.modelMapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);
		
		Location locationFromIP = locationService.getLocation(ipAddress);
		List<DailyWeather> dailyForecast = dailyWeatherService.getByLocation(locationFromIP);
		
		if (dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		DailyWeatherListDTO dto = listEntity2DTO(dailyForecast);
		
		return ResponseEntity.ok(addLinksByIP(dto));
	}
	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode) {
		List<DailyWeather> dailyForecast = dailyWeatherService.getByLocationCode(locationCode);
		
		if (dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		DailyWeatherListDTO dto = listEntity2DTO(dailyForecast);
		
		return ResponseEntity.ok(addLinksByLocation(dto, locationCode));
	}
	
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String code,
			@RequestBody @Valid List<DailyWeatherDTO> listDTO) throws BadRequestException {
		
		if (listDTO.isEmpty()) {
			throw new BadRequestException("Daily forecast data cannot be empty");
		}
		
		listDTO.forEach(System.out::println);
		
		List<DailyWeather> dailyWeather = listDTO2ListEntity(listDTO);
		
		System.out.println("================");
		
		dailyWeather.forEach(System.out::println);		
		
		List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(code, dailyWeather);
		
		DailyWeatherListDTO updatedDto = listEntity2DTO(updatedForecast);
		
		return ResponseEntity.ok(addLinksByLocation(updatedDto, code));
	}	
	
	private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
		Location location = dailyForecast.get(0).getId().getLocation();
		
		DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
		listDTO.setLocation(location.toString());
		
		dailyForecast.forEach(dailyWeather -> {
			listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
		});
		
		return listDTO;
	}
	
	private List<DailyWeather> listDTO2ListEntity(List<DailyWeatherDTO> listDTO) {
		List<DailyWeather> listEntity = new ArrayList<>();
		
		listDTO.forEach(dto -> {
			listEntity.add(modelMapper.map(dto, DailyWeather.class));
		});
		
		return listEntity;
	}	
	
	private EntityModel<DailyWeatherListDTO> addLinksByIP(DailyWeatherListDTO dto) {
		EntityModel<DailyWeatherListDTO> entityModel = EntityModel.of(dto);
		
		entityModel.add(linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).withSelfRel());

		entityModel.add(linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).withRel("realtime_weather"));

		entityModel.add(linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null)).withRel("hourly_forecast"));	

		entityModel.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withRel("full_forecast"));			
		
		return entityModel;
	}
	
	private EntityModel<DailyWeatherListDTO> addLinksByLocation(DailyWeatherListDTO dto, String locationCode) {
		
		return EntityModel.of(dto)
				
				.add(linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode)).withSelfRel())
		
				.add(linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode)).withRel("realtime_weather"))
		
				.add(linkTo(methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null)).withRel("hourly_forecast"))
		
				.add(linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withRel("full_forecast"));		
	}		
}