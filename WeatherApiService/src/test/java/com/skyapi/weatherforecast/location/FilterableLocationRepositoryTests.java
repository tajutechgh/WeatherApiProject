package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class FilterableLocationRepositoryTests {

	@Autowired
	private LocationRepository repository;
	
	@Test
	public void testListWithDefaults() {
		int pageSize = 5;
		int pageNum = 1;
		String sortField = "code";
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		
		Page<Location> page = repository.listWithFilter(pageable, Collections.emptyMap());
		
		List<Location> content = page.getContent();
		
		System.out.println("Total elements: " + page.getTotalElements());
		assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() + content.size());
		
		assertThat(content).size().isEqualTo(pageSize);
		
		assertThat(content).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location l1, Location l2) {
				return l1.getCode().compareTo(l2.getCode());
			}
		});
		
		content.forEach(System.out::println);
	}
	
	@Test
	public void testListNoFilterSortedByCityName() {
		int pageSize = 5;
		int pageNum = 0;
		String sortField = "cityName";
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		
		Page<Location> page = repository.listWithFilter(pageable, Collections.emptyMap());
		
		List<Location> content = page.getContent();
		
		System.out.println("Total elements: " + page.getTotalElements());
		assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() + content.size());
		
		assertThat(content).size().isEqualTo(pageSize);
		
		assertThat(content).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location l1, Location l2) {
				return l1.getCityName().compareTo(l2.getCityName());
			}
		});
		
		content.forEach(System.out::println);
	}	
	
	@Test
	public void testListFilteredRegionNameSortedByCityName() {
		int pageSize = 5;
		int pageNum = 0;
		String sortField = "cityName";
		String regionName = "California";
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String, Object> filterFields = new HashMap<>();
		filterFields.put("regionName", regionName);
		
		Page<Location> page = repository.listWithFilter(pageable, filterFields);
		
		List<Location> content = page.getContent();
		
		System.out.println("Total elements: " + page.getTotalElements());
		assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() + content.size());
		
		assertThat(content).size().isEqualTo(pageSize);
		
		assertThat(content).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location l1, Location l2) {
				return l1.getCityName().compareTo(l2.getCityName());
			}
		});
		
		content.forEach(location -> assertThat(location.getRegionName()).isEqualTo(regionName));
		
		content.forEach(System.out::println);
	}	
	
	@Test
	public void testListFilteredCountryCodeSortedByCode() {
		int pageSize = 1;
		int pageNum = 0;
		String sortField = "code";
		String countryCode = "US";
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String, Object> filterFields = new HashMap<>();
		filterFields.put("countryCode", countryCode);
		
		Page<Location> page = repository.listWithFilter(pageable, filterFields);
		
		List<Location> content = page.getContent();
		
		System.out.println("Total elements: " + page.getTotalElements());
		assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() + content.size());
		
		assertThat(content).size().isEqualTo(pageSize);
		
		assertThat(content).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location l1, Location l2) {
				return l1.getCode().compareTo(l2.getCode());
			}
		});
		
		content.forEach(location -> assertThat(location.getCountryCode()).isEqualTo(countryCode));
		
		content.forEach(System.out::println);
	}	
	
	@Test
	public void testListFilteredCountryCodeAndEnabledSortedByCityName() {
		int pageSize = 5;
		int pageNum = 0;
		String sortField = "cityName";
		
		String countryCode = "US";
		boolean enabled = true;
		
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		Map<String, Object> filterFields = new HashMap<>();
		filterFields.put("countryCode", countryCode);
		filterFields.put("enabled", enabled);
		
		Page<Location> page = repository.listWithFilter(pageable, filterFields);
		
		List<Location> content = page.getContent();
		
		System.out.println("Total elements: " + page.getTotalElements());
		assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() + content.size());
		
		assertThat(content).size().isEqualTo(pageSize);
		
		assertThat(content).isSortedAccordingTo(new Comparator<Location>() {

			@Override
			public int compare(Location l1, Location l2) {
				return l1.getCode().compareTo(l2.getCode());
			}
		});
		
		content.forEach(location -> { 
			assertThat(location.getCountryCode()).isEqualTo(countryCode);
			assertThat(location.isEnabled()).isTrue();
		});
		
		content.forEach(System.out::println);
	}		
}