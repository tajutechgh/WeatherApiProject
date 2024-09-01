package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {
	
	private static final String END_POINT_PATH = "/v1/locations";
	private static final String RESPONSE_CONTENT_TYPE = "application/hal+json";
	private static final String REQUEST_CONTENT_TYPE = "application/json";	
	
	@Autowired 
	MockMvc mockMvc;
	
	@Autowired 
	ObjectMapper mapper;
	
	@MockBean 
	LocationService service;
	
	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		LocationDTO location = new LocationDTO();
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testAddShouldReturn201Created() throws Exception {
		String code = "NYC_USA";
		Location location = new Location();
		location.setCode(code);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		LocationDTO dto = new LocationDTO();
		dto.setCode(location.getCode());
		dto.setCityName(location.getCityName());
		dto.setRegionName(location.getRegionName());
		dto.setCountryCode(location.getCountryCode());
		dto.setCountryName(location.getCountryName());
		dto.setEnabled(location.isEnabled());
		
		Mockito.when(service.add(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(dto);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isCreated())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is(code)))
			.andExpect(jsonPath("$.city_name", is("New York City")))
			.andExpect(header().string("Location", END_POINT_PATH + "/" + code))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost" + END_POINT_PATH + "/" + code)))
			.andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + code)))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + code)))
			.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + code)))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + code)))			
			.andDo(print());		
	}
	
	@Test
	public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
		
		Location location = new Location();
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);	
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errors[0]", is("Location code cannot be null")))
			.andDo(print());			
	}
	
	@Test
	public void testValidateRequestBodyLocationCodeLength() throws Exception {
		
		Location location = new Location();
		location.setCode("");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);	
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errors[0]", is("Location code must have 3-12 characters")))
			.andDo(print());			
	}
	
	@Test
	public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
		
		Location location = new Location();
		location.setRegionName("");
		
		String bodyContent = mapper.writeValueAsString(location);
		
		MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andDo(print())
			.andReturn();		
		
		String responseBody = mvcResult.getResponse().getContentAsString();
		
		assertThat(responseBody).contains("Location code cannot be null");
		assertThat(responseBody).contains("City name cannot be null");
		assertThat(responseBody).contains("Region name must have 3-128 characters");
		assertThat(responseBody).contains("Country name cannot be null");
		assertThat(responseBody).contains("Country code cannot be null");
	}
	
	@Test
	@Disabled
	public void testListShouldReturn204NoContent() throws Exception {
		
		Mockito.when(service.list()).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isNoContent())
				.andDo(print());
	}
	
	@Test
	@Disabled
	public void testListShouldReturn200OK() throws Exception {
		Location location1 = new Location();
		location1.setCode("NYC_USA");
		location1.setCityName("New York City");
		location1.setRegionName("New York");
		location1.setCountryCode("US");
		location1.setCountryName("United States of America");
		location1.setEnabled(true);		
		
		Location location2 = new Location();
		location2.setCode("LACA_USA");
		location2.setCityName("Los Angeles");
		location2.setRegionName("California");
		location2.setCountryCode("US");
		location2.setCountryName("United States of America");
		location2.setEnabled(true);	
		
		Mockito.when(service.list()).thenReturn(List.of(location1, location2));
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$[0].code", is("NYC_USA")))
			.andExpect(jsonPath("$[0].city_name", is("New York City")))
			.andExpect(jsonPath("$[1].code", is("LACA_USA")))
			.andExpect(jsonPath("$[1].city_name", is("Los Angeles")))			
			.andDo(print());			
	}
	
	@Test
	public void testListByPageShouldReturn204NoContent() throws Exception {
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap()))
						.thenReturn(Page.empty());
		
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isNoContent())
				.andDo(print());
	}
	
	@Test
	public void testListByPageShouldReturn200OK() throws Exception {
		Location location1 = new Location();
		location1.setCode("NYC_USA");
		location1.setCityName("New York City");
		location1.setRegionName("New York");
		location1.setCountryCode("US");
		location1.setCountryName("United States of America");
		location1.setEnabled(true);		
		
		Location location2 = new Location();
		location2.setCode("LACA_USA");
		location2.setCityName("Los Angeles");
		location2.setRegionName("California");
		location2.setCountryCode("US");
		location2.setCountryName("United States of America");
		location2.setEnabled(true);	
		
		List<Location> listLocations = List.of(location1, location2);
		
		int pageSize = 5;
		int pageNum = 1;
		String sortField = "code";
		int totalElements = listLocations.size();
		
		Sort sort = Sort.by(sortField);
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		
		Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap()))
					.thenReturn(page);
		
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
			.andExpect(jsonPath("$._embedded.locations[0].code", is("NYC_USA")))
			.andExpect(jsonPath("$._embedded.locations[0].city_name", is("New York City")))
			.andExpect(jsonPath("$._embedded.locations[1].code", is("LACA_USA")))
			.andExpect(jsonPath("$._embedded.locations[1].city_name", is("Los Angeles")))	
			.andExpect(jsonPath("$.page.size", is(pageSize)))
			.andExpect(jsonPath("$.page.number", is(pageNum)))
			.andExpect(jsonPath("$.page.total_elements", is(totalElements)))
			.andExpect(jsonPath("$.page.total_pages", is(1)))
			.andDo(print());			
	}	
	
	
	@Test
	public void testPaginationLinksOnlyOnePage() throws Exception {
		Location location1 = new Location("NYC_USA", "New York City", "New York", "US", "United States of America");
		Location location2 = new Location("LACA_USA", "Los Angeles", "California", "US", "United States of America");

		List<Location> listLocations = List.of(location1, location2);
		
		int pageSize = 5;
		int pageNum = 1;
		String sortField = "code";
		int totalElements = listLocations.size();
		
		Sort sort = Sort.by(sortField);
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		
		Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap())).thenReturn(page);
		
		String hostName = "http://localhost";
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
			.andExpect(jsonPath("$._links.self.href", containsString(hostName + requestURI)))
			.andExpect(jsonPath("$._links.first").doesNotExist())
			.andExpect(jsonPath("$._links.next").doesNotExist())
			.andExpect(jsonPath("$._links.prev").doesNotExist())
			.andExpect(jsonPath("$._links.last").doesNotExist())
			.andDo(print());			
	}		
	
	@Test
	public void testPaginationLinksInFirstPage() throws Exception {
		int totalElements = 18;
		int pageSize = 5;
		
		List<Location> listLocations = new ArrayList<>(pageSize);
		
		for (int i = 1; i <= pageSize; i++) {
			listLocations.add(new Location("CODE_" + i, "City " + i, "Region Name", "US", "Country Name"));
		}
		
		int pageNum = 1;
		int totalPages = totalElements / pageSize + 1;
		String sortField = "code";
		
		Sort sort = Sort.by(sortField);
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		
		Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap())).thenReturn(page);
		
		String hostName = "http://localhost";
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		String nextPageURI = END_POINT_PATH + "?page=" + (pageNum + 1) + "&size=" + pageSize + "&sort=" + sortField;
		String lastPageURI = END_POINT_PATH + "?page=" + totalPages + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
			.andExpect(jsonPath("$._links.first").doesNotExist())
			.andExpect(jsonPath("$._links.next.href", containsString(hostName + nextPageURI)))
			.andExpect(jsonPath("$._links.prev").doesNotExist())
			.andExpect(jsonPath("$._links.last.href", containsString(hostName + lastPageURI)))
			.andDo(print());			
	}	
	
	@Test
	public void testPaginationLinksInMiddlePage() throws Exception {
		int totalElements = 18;
		int pageSize = 5;
		
		List<Location> listLocations = new ArrayList<>(pageSize);
		
		for (int i = 1; i <= pageSize; i++) {
			listLocations.add(new Location("CODE_" + i, "City " + i, "Region Name", "US", "Country Name"));
		}
		
		int pageNum = 3;
		int totalPages = totalElements / pageSize + 1;
		String sortField = "code";
		
		Sort sort = Sort.by(sortField);
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		
		Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap())).thenReturn(page);
		
		String hostName = "http://localhost";
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		String firstPageURI = END_POINT_PATH + "?page=1&size=" + pageSize + "&sort=" + sortField;
		String nextPageURI = END_POINT_PATH + "?page=" + (pageNum + 1) + "&size=" + pageSize + "&sort=" + sortField;
		String prevPageURI = END_POINT_PATH + "?page=" + (pageNum - 1) + "&size=" + pageSize + "&sort=" + sortField;
		String lastPageURI = END_POINT_PATH + "?page=" + totalPages + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
			.andExpect(jsonPath("$._links.first.href", containsString(hostName + firstPageURI)))
			.andExpect(jsonPath("$._links.next.href", containsString(hostName + nextPageURI)))
			.andExpect(jsonPath("$._links.prev.href", containsString(hostName + prevPageURI)))
			.andExpect(jsonPath("$._links.last.href", containsString(hostName + lastPageURI)))
			.andDo(print());			
	}		

	@Test
	public void testPaginationLinksInLastPage() throws Exception {
		int totalElements = 18;
		int pageSize = 5;
		
		List<Location> listLocations = new ArrayList<>(pageSize);
		
		for (int i = 1; i <= pageSize; i++) {
			listLocations.add(new Location("CODE_" + i, "City " + i, "Region Name", "US", "Country Name"));
		}
		
		int totalPages = (totalElements / pageSize) + 1;
		int pageNum = totalPages;
		String sortField = "code";
		
		Sort sort = Sort.by(sortField);
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		
		Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap())).thenReturn(page);
		
		String hostName = "http://localhost";
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		String firstPageURI = END_POINT_PATH + "?page=1&size=" + pageSize + "&sort=" + sortField;
		String prevPageURI = END_POINT_PATH + "?page=" + (pageNum - 1) + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
			.andExpect(jsonPath("$._links.first.href", containsString(hostName + firstPageURI)))
			.andExpect(jsonPath("$._links.next").doesNotExist())
			.andExpect(jsonPath("$._links.prev.href", containsString(hostName + prevPageURI)))
			.andExpect(jsonPath("$._links.last").doesNotExist())
			.andDo(print());			
	}		
	
	@Test
	public void testListByPageShouldReturn400BadRequestInvalidPageNum() throws Exception {
		int pageNum = 0;
		int pageSize = 5;
		String sortField = "code";
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap()))
						.thenReturn(Page.empty());
		
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", containsString("must be greater than or equal to 1")))
				.andDo(print());
	}
	
	@Test
	public void testListByPageShouldReturn400BadRequestInvalidPageSize() throws Exception {
		int pageNum = 1;
		int pageSize = 3;
		String sortField = "code";
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap()))
						.thenReturn(Page.empty());
		
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", containsString("must be greater than or equal to 5")))
				.andDo(print());
	}	
	
	@Test
	public void testListByPageShouldReturn400BadRequestInvalidSortField() throws Exception {
		int pageNum = 1;
		int pageSize = 5;
		String sortField = "code_abc";
		
		Mockito.when(service.listByPage(anyInt(), anyInt(), anyString(), anyMap()))
						.thenReturn(Page.empty());
		
		String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
		
		mockMvc.perform(get(requestURI))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", containsString("invalid sort field")))
				.andDo(print());
	}	
	
	@Test
	public void testGetShouldReturn405MethodNotAllowed() throws Exception {
		String requestURI = END_POINT_PATH + "/ABCDEF";
		
		mockMvc.perform(post(requestURI))
			.andExpect(status().isMethodNotAllowed())
			.andDo(print());			
	}
	
	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		String locationCode = "ABCDEF";
		String requestURI = END_POINT_PATH + "/"+ locationCode;
		
		Mockito.when(service.get(locationCode)).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());			
	}
	
	@Test
	public void testGetShouldReturn200OK() throws Exception {
		String code = "MBMH_IN";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Location location = new Location();
		location.setCode("LACA_USA");
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);	
		
		Mockito.when(service.get(code)).thenReturn(location);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is(code)))
			.andExpect(jsonPath("$.city_name", is("Los Angeles")))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost" + END_POINT_PATH + "/" + code)))
			.andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + code)))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + code)))
			.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + code)))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + code)))
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCode("ABCDEF");
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);			
		
		LocationNotFoundException ex = new LocationNotFoundException(location.getCityName());
		
		Mockito.when(service.update(Mockito.any())).thenThrow(ex);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/jason").content(bodyContent))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);			
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/jason").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn200OK() throws Exception {
		String code = "NYC_USA";
		Location location = new Location();
		location.setCode(code);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		LocationDTO dto = new LocationDTO();
		dto.setCode(location.getCode());
		dto.setCityName(location.getCityName());
		dto.setRegionName(location.getRegionName());
		dto.setCountryCode(location.getCountryCode());
		dto.setCountryName(location.getCountryName());
		dto.setEnabled(location.isEnabled());		
		
		Mockito.when(service.update(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(dto);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/jason").content(bodyContent))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/jason"))
			.andExpect(jsonPath("$.code", is("NYC_USA")))
			.andExpect(jsonPath("$.city_name", is("New York City")))
			.andExpect(jsonPath("$._links.self.href", is("http://localhost" + END_POINT_PATH + "/" + code)))
			.andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + code)))
			.andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + code)))
			.andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + code)))
			.andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + code)))			
			.andDo(print());		
	}	
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		LocationNotFoundException ex = new LocationNotFoundException(code);
		Mockito.doThrow(ex).when(service).delete(code);
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Mockito.doNothing().when(service).delete(code);
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
}
