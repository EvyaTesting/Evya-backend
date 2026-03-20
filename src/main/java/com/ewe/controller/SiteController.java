package com.ewe.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
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
import com.ewe.form.FavSiteDTO;
import com.ewe.form.SiteDetailsForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.Site;
import com.ewe.pojo.SiteOperationalDetails;
import com.ewe.pojo.Station;
import com.ewe.service.SiteService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Scope("request")
@Hidden
@Api(tags = "SiteController")
@RequestMapping("/services/site")
public class SiteController {

	@Autowired
	private SiteService siteService;

	@ApiOperation(value = "Add Site")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> addSite(@RequestBody(required = false) SiteDetailsForm siteForm)
			throws UserNotFoundException {
		siteService.addSite(siteForm);
		String msg = "Site Successfully Created";
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
	}

	@ApiOperation(value = "Get Site Details By Id")
	@RequestMapping(value = "/siteDetails/{id}", method = RequestMethod.GET)
	public ResponseEntity<SiteDetailsForm> getProfileById(@PathVariable Long id)
	        throws UserNotFoundException {
	    return ResponseEntity.status(HttpStatus
	    		.OK).body(siteService.getSiteById(id));
	}

	@ApiOperation(value = "Edit Site")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> editSite(@PathVariable Long id, @RequestBody(required = false) SiteDetailsForm siteForm)
			throws UserNotFoundException {
		siteService.editSite(id, siteForm);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Site Successfully Updated"));
	}

	@ApiOperation(value = "Site List with organization filtering")
	@RequestMapping(value = "/siteList", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllSites(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) Long orgId,
	        @RequestParam(required=false)String search) {
	    	
	    List<Map<String,Object>> allSites = siteService.getAllSitesPaginated(orgId,search);
	    int start = page * size;
	    int end = Math.min(start + size, allSites.size());	    
	    if (start >= allSites.size() && allSites.size() > 0) {
	        start = 0;
	        end = Math.min(size, allSites.size());
	    }
	    List<Map<String,Object>> pagedSites = start < end ? allSites.subList(start, end) : new ArrayList<>();
	    Map<String, Object> response = new HashMap<>();
	    response.put("sites", pagedSites);
	    response.put("currentPage", page);
	    response.put("totalItems", allSites.size());
	    response.put("totalPages", (int) Math.ceil((double) allSites.size() / size));
	    
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get all sites with organization filtering")
	@RequestMapping(value = "/getAllSites", method = RequestMethod.GET)
	public ResponseEntity<List<Site>> getAllSites(
	        @RequestParam(required = false) Long orgId)
	        throws UserNotFoundException, InterruptedException {
	    return ResponseEntity.status(HttpStatus.OK).body(siteService.getAllSites(orgId));
	}
	
	@ApiOperation(value = "Get Site By Org_Id")
	@GetMapping("/siteDetailsbyOrg/{id}")
	public ResponseEntity<List<Site>> getOrgProfileById(@PathVariable Long id)
	        throws UserNotFoundException {
	    List<Site> sites = siteService.getSiteByOrgId(id);
	    return ResponseEntity.ok(sites);
	}
	
	@ApiOperation(value = "Get id and name from dynamic table with organization/whitelabel filtering")
	@GetMapping("/getAllByTable")
	public ResponseEntity<?> getAllByTable(
	    @RequestParam String tableName,
	    @RequestParam(required = false) Long OrgId) {  // This can be orgId or whitelabelId
	    return ResponseEntity.ok(siteService.getAllByTable(tableName, OrgId));
	}
	
		@ApiOperation(value = "Get stations By siteId")
		@RequestMapping(value = "/stationDetailsbysiteid/{id}", method = RequestMethod.GET)
		public ResponseEntity<List<Station>> getProfileBysiteId(@PathVariable Long id)
				throws UserNotFoundException, InterruptedException {
			return ResponseEntity.status(HttpStatus.OK).body(siteService.getStationBysiteId(id));
		}	
		@ApiOperation(value = " change the site status ")
		@RequestMapping(value = "/site-operations/edit", method = RequestMethod.PUT)
		public ResponseEntity<?> updateSiteOperationalDetails(@RequestBody SiteOperationalDetails updatedDetails) {
		    try {
		    	siteService.updateSiteOperationalDetails(updatedDetails);
		        return ResponseEntity.ok("SiteOperationalDetails updated successfully.");
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update.");
		    }
		}
		
		@PostMapping("/addFavSite/{userId}")
		public ResponseEntity<?> addFavSite(
		        @PathVariable Long userId,
		        @RequestBody AddFavSiteDTO dto) throws UserNotFoundException {
		    try {
		        siteService.addFavSiteForUser(userId, dto.getSiteId());
		        return ResponseEntity.status(HttpStatus.CREATED)
		                .body(new ResponseMessage("Added to favourites"));
		    } 
		    catch (IllegalArgumentException e) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                .body(new ResponseMessage(e.getMessage()));
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new ResponseMessage("Error adding favourite site"));
		    }
		}
		
		@GetMapping("/getFavSite/user/{userId}")
		public ResponseEntity<?> getFavStationsByUserId(@PathVariable Long userId) {
		    try {
		        List<FavSiteDTO> favStations = siteService.getFavStationsByUserId(userId);
		        if (favStations.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND)
		                    .body(new ResponseMessage("No favourite stations found for this user"));
		        }
		        return ResponseEntity.ok(favStations);
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new ResponseMessage("Error fetching favourite stations"));
		    }
		}
		
		@DeleteMapping("/deleteFavSite/{userId}")
		public ResponseEntity<?> deleteFavStations(@PathVariable Long userId) {
		    try {
		        siteService.deleteFavSitesByUserId(userId);
		        return ResponseEntity.ok(new ResponseMessage("Deleted successfully"));
		    } catch (UserNotFoundException e) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND)
		                .body(new ResponseMessage(e.getMessage()));
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new ResponseMessage("Error deleting favourite stations"));
		    }
		}
		

}