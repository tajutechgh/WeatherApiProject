package com.skyapi.weatherforecast.daily;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class DailyWeatherRepositoryTests {

	private @Autowired DailyWeatherRepository repo;
	
	@Test
	public void testAdd() {
		String locationCode = "LACA_USA";
		
		Location location = new Location().code(locationCode);
		
		DailyWeather forecast = new DailyWeather()
				.location(location)
				.dayOfMonth(16)
				.month(7)
				.minTemp(23)
				.maxTemp(32)
				.precipitation(40)
				.status("Cloudy");	
		
		DailyWeather addedForecast = repo.save(forecast);
		
		assertThat(addedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
	}
	
	@Test
	public void testDelete() {
		String locationCode = "MADRID_ES";
		
		Location location = new Location().code(locationCode);
		
		DailyWeatherId id = new DailyWeatherId(16, 7, location);
		
		repo.deleteById(id);
		
		Optional<DailyWeather> result = repo.findById(id);
		
		assertThat(result).isNotPresent();
	}
	
	@Test
	public void testFindByLocationCodeFound() {
		String locationCode = "MADRID_ES";
		
		List<DailyWeather> dailyWeather = repo.findByLocationCode(locationCode);
		
		assertThat(dailyWeather).isNotEmpty();
	}
	
	@Test
	public void testFindByLocationCodeNotFound() {
		String locationCode = "ABC_XYZ";
		
		List<DailyWeather> dailyWeather = repo.findByLocationCode(locationCode);
		
		assertThat(dailyWeather).isEmpty();
	}	
}