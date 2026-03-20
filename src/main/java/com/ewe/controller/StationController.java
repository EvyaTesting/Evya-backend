package com.ewe.controller;

import java.io.IOException;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.AddFavSiteDTO;
import com.ewe.form.EvStationDto;
import com.ewe.form.PortStatusResponse;
import com.ewe.form.RequestedFranchisesDTO;
import com.ewe.form.SiteDetailsForm;
import com.ewe.form.StationDetailsForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.Port;
import com.ewe.pojo.RequestedFranchises;
import com.ewe.pojo.Site;
import com.ewe.pojo.Station;
import com.ewe.service.StationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Scope("request")
@Hidden
@Api(tags = "StationController")
@RequestMapping("/services/station")
public class StationController {
	
	@Autowired
	private StationService stnService;

	@ApiOperation(value = "Add station")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> addStation(@RequestBody(required = false) StationDetailsForm siteForm)
			throws UserNotFoundException {
		stnService.addStation(siteForm);
		String msg = "Station Successfully Created";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
	}
	
	@ApiOperation(value = "edit station")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> editStation(@PathVariable Long id,@RequestBody(required = false) StationDetailsForm form)
			throws UserNotFoundException {
		stnService.editStation(id, form);
		String msg = "Station Successfully Created";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
	}
	
	@ApiOperation(value = "Get Site By Id")
	@RequestMapping(value = "/stationDetails/{id}", method = RequestMethod.GET)
	public ResponseEntity<Station> getProfileById(@PathVariable Long id)
			throws UserNotFoundException, InterruptedException {
		return ResponseEntity.status(HttpStatus.OK).body(stnService.getStationById(id));
	}
	
	@ApiOperation(value = "Get all station ")
	@RequestMapping(value = "/Stations", method = RequestMethod.GET)
	public ResponseEntity<List<Station>> getAllSites(
	        @RequestParam(required = false) Long orgId)
	        throws UserNotFoundException, InterruptedException {
	    return ResponseEntity.status(HttpStatus.OK).body(stnService.getAllStations(orgId));
	}
	
	@ApiOperation(value = "Get stations with pagination, organization filtering and search")
	@RequestMapping(value = "/stationList", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllStations(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) Long orgId,
	        @RequestParam(required = false) String search) {
	    
	    // Get all stations with optional org filtering and search
	    List<Station> allStations = stnService.getAllStationsPaginated(orgId, search);
	    
	    // Rest of your existing pagination logic remains the same
	    int start = page * size;
	    int end = Math.min(start + size, allStations.size());
	    if (start >= allStations.size() && allStations.size() > 0) {
	        start = 0;
	        end = Math.min(size, allStations.size());
	    }
	    List<Station> pagedStations = start < end ? allStations.subList(start, end) : new ArrayList<>();
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("stations", pagedStations);
	    response.put("currentPage", page);
	    response.put("totalItems", allStations.size());
	    response.put("totalPages", (int) Math.ceil((double) allStations.size() / size));
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@ApiOperation(value = "change the station status")
	@RequestMapping(value = "/station_status", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> updateStationStatus(@RequestParam Long stationId,
            @RequestParam String stationStatus)
			throws UserNotFoundException {
		stnService.updateStationStatus(stationId, stationStatus);
		String msg = "Station status updated successfully";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
	}
	
	@ApiOperation(value = "Search stations by site name, status, and current type")
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> searchStations(
	       @RequestParam(required = false) String siteName, // Changed from stationName
	       @RequestParam(required = false) String stationStatus,
	       @RequestParam(required = false) String currentType,
	       @RequestParam(required = false) Long orgId,
	       @RequestParam(defaultValue = "0") int page,
	       @RequestParam(defaultValue = "10") int size) {

	   List<Station> pagedResults = stnService.searchStations(siteName, stationStatus, currentType, orgId, page, size);
	   int totalItems = stnService.countStations(siteName, stationStatus, currentType, orgId);

	   Map<String, Object> response = new HashMap<>();
	   response.put("stations", pagedResults);
	   response.put("currentPage", page);
	   response.put("totalItems", totalItems);
	   response.put("totalPages", (int) Math.ceil((double) totalItems / size));

	   return ResponseEntity.ok(response);
	}
	
	@ApiOperation(value = "mobile based get sites and stations")
	@RequestMapping(value = "/getstations", method = RequestMethod.GET)
	
    public ResponseEntity<List<Map<String, Object>>> getAllSitesSummary() {
        List<Map<String, Object>> sitesSummary = stnService.getAllSitesSummary();
        return ResponseEntity.ok(sitesSummary);
    }	
	
	@ApiOperation(value = "get station details from google api")
	@RequestMapping(value = "/getcharging-stations", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getStations(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "5000") int radius,
            @RequestParam(required = false) String connectorType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String powerType,
            @RequestParam(required = false) Integer minPower) {

        List<Map<String, Object>> stations = stnService.getStations(
                latitude, longitude, address, from, to, radius,
                connectorType, status, powerType, minPower);

        return stations.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(stations);
    }
	
	@ApiOperation(value = "request a franchise, site, or station")
	@PostMapping("/request-franchise")
	public ResponseEntity<List<RequestedFranchises>> createRequest(@RequestBody RequestedFranchisesDTO request) throws UserNotFoundException {    
	    switch(request.getCategory().toUpperCase()) {
	        case "FRANCHISE":
	            break;
	        case "SITE":
	            if(request.getFranchiseName() == null || request.getFranchiseName().isEmpty()) {
	                return ResponseEntity.badRequest().body(null);
	            }
	            break;
	        case "STATION":
	            if(request.getFranchiseName() == null || request.getFranchiseName().isEmpty() ||
	               request.getSites() == null || request.getSites().isEmpty()) {
	                return ResponseEntity.badRequest().body(null);
	            }
	            break;
	        default:
	            return ResponseEntity.badRequest().body(null);
	    }	    
	    List<RequestedFranchises> saved = stnService.saveRequestedFranchise(request);
	    return ResponseEntity.ok(saved);
	}
	
	@ApiOperation(value = "Requested Franchises/Sites/Stations List")
	@RequestMapping(value = "/requestedFranchisesList", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllRequestedFranchises(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String category ) throws UserNotFoundException {

	    // Get all requests with optional category/search filter
	    List<Map<String,Object>> allRequests = stnService.getAllRequestedFranchisesAsMap(category);

	    int start = page * size;
	    int end = Math.min(start + size, allRequests.size());

	    if (start >= allRequests.size() && allRequests.size() > 0) {
	        start = 0;
	        end = Math.min(size, allRequests.size());
	    }
	    List<Map<String,Object>> pagedRequests = start < end ? allRequests.subList(start, end) : new ArrayList<>();
	    Map<String, Object> response = new HashMap<>();
	    response.put("requests", pagedRequests);
	    response.put("currentPage", page);
	    response.put("totalItems", allRequests.size());
	    response.put("totalPages", (int) Math.ceil((double) allRequests.size() / size));

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

//	@GetMapping("/getstationsjson")
//	public ResponseEntity<List<Map<String, Object>>> getAllStations(
//	        @RequestParam(required = false) Double latitude,
//	        @RequestParam(required = false) Double longitude,
//	        @RequestParam(required = false) Double radiusKm,
//	        @RequestParam(required = false) String nameFilter,
//	        @RequestParam(required = false) String fromAddress,
//	        @RequestParam(required = false) String toAddress,
//	        @RequestParam(required = false) String search) throws IOException {
//
//	    List<Map<String, Object>> response = stnService.getAllStationsJson(
//	            latitude, longitude, radiusKm, nameFilter, fromAddress, toAddress, search);
//	    return ResponseEntity.ok(response);
//	}

	@GetMapping("/getstationsjson")
	@ApiOperation(value = "Get sites and stations combined (mobile + JSON-based)")
	public ResponseEntity<List<Map<String, Object>>> getAllStationsAndSites(
	        @RequestParam(required = false) Double latitude,
	        @RequestParam(required = false) Double longitude,
	        @RequestParam(required = false) Double radiusKm,
	        @RequestParam(required = false) String nameFilter,
	        @RequestParam(required = false) String fromAddress,
	        @RequestParam(required = false) String toAddress,
	        @RequestParam(required = false) String search) throws IOException {

	    List<Map<String, Object>> stationsJson = stnService.getAllStationsJson(
	            latitude, longitude, radiusKm, nameFilter, fromAddress, toAddress, search);

	    List<Map<String, Object>> sitesSummary = stnService.getAllSitesSummary();

	    List<Map<String, Object>> finalResponse = new ArrayList<>();
	    finalResponse.addAll(stationsJson);
	    finalResponse.addAll(sitesSummary);

	    return ResponseEntity.ok(finalResponse);
	}
	
	@ApiOperation(value = "Get requested franchise/site/station by ID")
	@GetMapping("/requestedFranchise/{id}")
	public ResponseEntity<RequestedFranchises> getRequestedFranchiseById(
	        @PathVariable Long id) throws UserNotFoundException {
	    RequestedFranchises request = stnService.getRequestedFranchiseById(id);
	    return ResponseEntity.ok(request);
	}
	
	@ApiOperation(value = "Get all requested franchise/site/station by User ID")
	@GetMapping("/requestedFranchise/user/{userId}")
	public ResponseEntity<List<RequestedFranchises>> getRequestedFranchisesByUserId(
	        @PathVariable Long userId) throws UserNotFoundException {
	    List<RequestedFranchises> requests = stnService.getRequestedFranchisesByUserId(userId);
	    return ResponseEntity.ok(requests);
	}
	
	@GetMapping("/pending-requests-count")
	public ResponseEntity<Map<String, Object>> getPendingRequestsCount() {
	    Map<String, Object> counts = stnService.getPendingRequestsCount();
	    return ResponseEntity.ok(counts);
	}
	
	// sukanya added api to count the json requests
	@GetMapping("/json-count")
	public ResponseEntity<Map<String, Object>> getStationJsonCount() {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        InputStream inputStream = getClass().getResourceAsStream("/data/ev_stations.json");
	        Map<String, Object> jsonData = mapper.readValue(inputStream, Map.class);
	        List<Map<String, Object>> stations = (List<Map<String, Object>>) jsonData.get("ev_stations");

	        int count = (stations != null) ? stations.size() : 0;
	        response.put("count", count);
	        response.put("message", "Successfully counted EV stations from JSON file");

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        response.put("error", "Failed to read or parse ev_stations.json");
	        response.put("details", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@PostMapping("/verify-and-import")
    public ResponseEntity<String> verifyAndImportStations(@RequestBody EvStationDto evStationData) {
        try {
        	stnService.verifyAndProcessEvStations(evStationData.getEvStations());
            return ResponseEntity.ok("EV stations verified and imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing EV stations: " + e.getMessage());
        }
    }
	
	@PutMapping("/{portId}/price")
	public ResponseEntity<?> updatePortPrice(
	        @PathVariable long portId,
	        @RequestParam Double billingamount) {
	    Port updatedPort = stnService.updatePortPrice(portId, billingamount);
	    if (updatedPort != null) {
	        return ResponseEntity.ok(updatedPort);
	    } else {
	        return ResponseEntity.badRequest()
	                .body("Failed to update billing amount. Port not found or invalid data.");
	    }
	}
	
	@GetMapping("/{portId}/status-details")
	public ResponseEntity<?> getPortStatusWithReservationAndPricing(
	        @PathVariable Long portId,
	        @RequestParam(required = false) String calculationTime) {

	    try {

	        Instant calculationInstant;
	        if (calculationTime != null && !calculationTime.trim().isEmpty()) {
	            LocalDateTime ldt = LocalDateTime.parse(calculationTime);
	            ZonedDateTime istZdt = ldt.atZone(ZoneId.of("Asia/Kolkata"));
	            calculationInstant = istZdt.toInstant();
	            System.out.println("=== DEBUG ===");
	            System.out.println("Input IST: " + istZdt);
	            System.out.println("Converted UTC for processing: " + calculationInstant);

	        } else {
	            calculationInstant = Instant.now(); // default UTC
	        }

	        Date calculationDate = Date.from(calculationInstant);

	        PortStatusResponse response =
	                stnService.getPortStatusWithReservationAndPricing(portId, calculationDate);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
	    }
	}
	
	@GetMapping("/status/{portId}")
	public ResponseEntity<String>getPortStatus(@PathVariable Long portId){
		String status =stnService.getCurrentPortStatus(portId);
		 return ResponseEntity.ok(status);
	}
	
	    @GetMapping("/station/{stationId}/{filename}")
	    public ResponseEntity<byte[]> getLogFile(
	            @PathVariable String stationId,
	            @PathVariable String filename) {

	        try {
	            byte[] data = stnService.downloadLogFile(stationId, filename);

	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	                    .contentType(MediaType.TEXT_PLAIN)
	                    .body(data);

	        } catch (Exception e) {
	            return ResponseEntity.status(500).body(null);
	        }
	    }
}
