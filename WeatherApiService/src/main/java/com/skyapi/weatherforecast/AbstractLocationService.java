package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

public abstract class AbstractLocationService {
	
	protected LocationRepository locationRepo;
	
	public Location get(String code) {
		
		Location location = locationRepo.findByCode(code);
		
		if (location == null) {
			
			throw new LocationNotFoundException(code);
		}
		
		return location;
	}	
}