package com.skyapi.weatherforecast.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"locations_url", "location_by_code_url", "realtime_weather_by_ip_url",
	"realtime_weather_by_code_url", "hourly_forecast_by_ip_url", "hourly_forecast_by_code_url",
	"daily_forecast_by_ip_url", "daily_forecast_by_code_url",
	"full_weather_by_ip_url", "full_weather_by_code_url"})
public class RootEntity {
	
	private String locationsUrl;
	
	private String locationByCodeUrl;
	
	private String realtimeWeatherByIpUrl;

	private String realtimeWeatherByCodeUrl;
	
	private String hourlyForecastByIpUrl;	
	
	private String hourlyForecastByCodeUrl;	
	
	private String dailyForecastByIpUrl;	
	
	private String dailyForecastByCodeUrl;	
	
	private String fullWeatherByIpUrl;	
	
	private String fullWeatherByCodeUrl;		
	
	public String getLocationsUrl() {
		return locationsUrl;
	}

	public void setLocationsUrl(String locationsUrl) {
		this.locationsUrl = locationsUrl;
	}

	public String getLocationByCodeUrl() {
		return locationByCodeUrl;
	}

	public void setLocationByCodeUrl(String locationByCodeUrl) {
		this.locationByCodeUrl = locationByCodeUrl;
	}

	public String getRealtimeWeatherByIpUrl() {
		return realtimeWeatherByIpUrl;
	}

	public void setRealtimeWeatherByIpUrl(String realtimeWeatherByIpUrl) {
		this.realtimeWeatherByIpUrl = realtimeWeatherByIpUrl;
	}

	public String getRealtimeWeatherByCodeUrl() {
		return realtimeWeatherByCodeUrl;
	}

	public void setRealtimeWeatherByCodeUrl(String realtimeWeatherByCodeUrl) {
		this.realtimeWeatherByCodeUrl = realtimeWeatherByCodeUrl;
	}

	public String getHourlyForecastByIpUrl() {
		return hourlyForecastByIpUrl;
	}

	public void setHourlyForecastByIpUrl(String hourlyForecastByIpUrl) {
		this.hourlyForecastByIpUrl = hourlyForecastByIpUrl;
	}

	public String getHourlyForecastByCodeUrl() {
		return hourlyForecastByCodeUrl;
	}

	public void setHourlyForecastByCodeUrl(String hourlyForecastByCodeUrl) {
		this.hourlyForecastByCodeUrl = hourlyForecastByCodeUrl;
	}

	public String getDailyForecastByIpUrl() {
		return dailyForecastByIpUrl;
	}

	public void setDailyForecastByIpUrl(String dailyForecastByIpUrl) {
		this.dailyForecastByIpUrl = dailyForecastByIpUrl;
	}

	public String getDailyForecastByCodeUrl() {
		return dailyForecastByCodeUrl;
	}

	public void setDailyForecastByCodeUrl(String dailyForecastByCodeUrl) {
		this.dailyForecastByCodeUrl = dailyForecastByCodeUrl;
	}

	public String getFullWeatherByIpUrl() {
		return fullWeatherByIpUrl;
	}

	public void setFullWeatherByIpUrl(String fullWeatherByIpUrl) {
		this.fullWeatherByIpUrl = fullWeatherByIpUrl;
	}

	public String getFullWeatherByCodeUrl() {
		return fullWeatherByCodeUrl;
	}

	public void setFullWeatherByCodeUrl(String fullWeatherByCodeUrl) {
		this.fullWeatherByCodeUrl = fullWeatherByCodeUrl;
	}
	
	
}