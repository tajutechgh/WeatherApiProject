package com.skyapi.weatherforecast.location;

import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotNull;

@JsonPropertyOrder({"code", "city_name", "region_name", "country_code", "country_name", "enabled"})
@Relation(collectionRelation = "locations")
public class LocationDTO extends CollectionModel<LocationDTO> {
	
	@NotNull(message = "Location code cannot be null")
	@Length(min = 3, max = 12, message = "Location code must have 3-12 characters")
	private String code;

	@NotNull(message = "City name cannot be null")
	@Length(min = 3, max = 128, message = "City name must have 3-128 characters")
	private String cityName;

	@Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String regionName;

	@NotNull(message = "Country name cannot be null")
	@Length(min = 3, max = 64, message = "Country name must have 3-64 characters")
	private String countryName;

	@NotNull(message = "Country code cannot be null")
	@Length(min = 2, max = 2, message = "Country code must have 2 characters")
	private String countryCode;

	private boolean enabled;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}