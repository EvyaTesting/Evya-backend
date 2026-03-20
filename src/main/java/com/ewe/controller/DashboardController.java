package com.ewe.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.pojo.ChargingActivity;
import com.ewe.service.DashboardService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
@Scope("request")
@RequestMapping("/services/dashboard")
public class DashboardController {
	
	@Autowired
	private DashboardService dashboardService;	
	
	@RequestMapping(value = "stats/{orgId}", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, Object>> getStats(
	        @PathVariable int orgId,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
	        		throws ParseException {
		return dashboardService.getStats(orgId, startDate, endDate);
	}
		
	@ApiOperation(value = "Generate revenueGraph based on organization Id")
	@RequestMapping(value = "revenueGraph/{orgId}", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, Object>> revenueGraph(@PathVariable int orgId,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
	        		throws ParseException {
		return dashboardService.getRevenueGraph(orgId, startDate, endDate);
	}
	
	@ApiOperation(value = "Generate sessionGraph based on organization Id")
	@RequestMapping(value = "sessionGraph/{orgId}", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, Object>> sessionGraph(@PathVariable int orgId, 
	@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    		throws ParseException {
		return  dashboardService.getSessionGraph(orgId, startDate, endDate);
	}
	
	@ApiOperation(value = "Generate stationStats based on organization Id")
	@RequestMapping(value = "stationStats/{orgId}", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, Object>> stationStats(@PathVariable int orgId, 
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
		    		throws ParseException {
		return dashboardService.getStationStats(orgId, startDate, endDate);
	}
	
	@ApiOperation(value = "Generate portStats based on organization Id")
	@RequestMapping(value = "portStats/{orgId}", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map<String, Object>> portStats(@PathVariable int orgId, 
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
		    		throws ParseException {
		return dashboardService.getPortStats(orgId, startDate, endDate);
	}
} 
//anitha's backend'