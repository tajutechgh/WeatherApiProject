package com.skyapi.weatherforecast;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;

@Service
public class GeolocationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
	
	private String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	
	private IP2Location ipLocator = new IP2Location();

	public GeolocationService() {
		try {
			InputStream inputStream = getClass().getResourceAsStream(DBPath);
			byte[] data = inputStream.readAllBytes();
			ipLocator.Open(data);
			inputStream.close();			
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}
	
	public Location getLocation(String ipAddress) throws GeolocationException {
		
		try {
			IPResult result = ipLocator.IPQuery(ipAddress);
			
			if (!"OK".equals(result.getStatus())) {
				throw new GeolocationException("Geolocation failed with status: " + result.getStatus());
			}
			
			LOGGER.info(result.toString());
			
			return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());
			
		} catch (IOException ex) {
			throw new GeolocationException("Error querying IP database", ex);
		}
		
	}
	
}