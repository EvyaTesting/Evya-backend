package com.ewe.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.controller.advice.ServerException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ManufacturerForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.ManufacturerDetails;
import com.ewe.pojo.Site;
import com.ewe.service.ManufacturerService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Scope("request")
@Hidden
@RequestMapping("/services/manufacturer")
public class ManufacturerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManufacturerController.class);
	
	@Autowired
	private ManufacturerService manufacturerService;	
	
	@ApiOperation(value = "Insert Profile")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> add(
			@RequestBody ManufacturerForm mannform)
			throws UserNotFoundException, ServerException, ParseException {
		logger.info("ManufacturerController.add() - with [" + mannform + "]");
		manufacturerService.add(mannform);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Manufacturer Added Successfully"));
	}
	@ApiOperation(value = "add charger deatils")
	@RequestMapping(value = "/update{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> addCharger(@PathVariable Long id,
			@RequestBody ManufacturerForm manform)
			throws UserNotFoundException, ServerException, ParseException {
		logger.info("ManufacturerController.add() - with [" + manform + "]");
		manufacturerService.addCharger(id,manform);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Manufacturer updated Successfully"));
	}

	@ApiOperation(value = "Get All profile")
	@RequestMapping(value = "/getManufracturer", method = RequestMethod.GET)
	public ResponseEntity<List<ManufacturerDetails>> getProfile() throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.OK).body(manufacturerService.getALLManufacturers());
	}
	
	@ApiOperation(value = "Get Manufacturer By Id")
	@RequestMapping(value = "/getManufracturer/{id}", method = RequestMethod.GET)
	public ResponseEntity<ManufacturerDetails> getProfileById(@PathVariable Long id)
			throws UserNotFoundException, InterruptedException {
		return ResponseEntity.status(HttpStatus.OK).body(manufacturerService.getManufracturerById(id));
	}
	
	@ApiOperation(value = "getAllManufacturer")
	@RequestMapping(value = "/getAllManufacturer", method = RequestMethod.GET)
	public ResponseEntity<List<ManufacturerDetails>> getAllSites()
			throws UserNotFoundException, InterruptedException {
		return ResponseEntity.status(HttpStatus.OK).body(manufacturerService.getALLManufacturers());
	}
	
	@ApiOperation(value = "Manufacturer List with pagination and search")
	@RequestMapping(value = "/manufacturerList", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllManufacturerPaginated(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String search) throws UserNotFoundException {
	    
	    List<ManufacturerDetails> allManufacturers = manufacturerService.getAllManufacturerPaginated(search);	    
	    int start = page * size;
	    int end = Math.min(start + size, allManufacturers.size());
	    
	    if (start >= allManufacturers.size() && allManufacturers.size() > 0) {
	        start = 0;
	        end = Math.min(size, allManufacturers.size());
	    }
	    
	    List<ManufacturerDetails> pagedManufacturers = start < end ? allManufacturers.subList(start, end) : new ArrayList<>();
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("manufacturers", pagedManufacturers);
	    response.put("currentPage", page);
	    response.put("totalItems", allManufacturers.size());
	    response.put("totalPages", (int) Math.ceil((double) allManufacturers.size() / size));
	    
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}}