package com.ewe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ReferralCodeRequest;
import com.ewe.form.SiteDetailsForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.Referral;
import com.ewe.pojo.Response;
import com.ewe.pojo.Site;
import com.ewe.service.ReferralService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/services/referral-codes")
public class ReferralController {

    @Autowired
    private ReferralService referralCodeService;
    
    @ApiOperation(value = "Add ReferralCode")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Referral> createReferralCode(@RequestBody ReferralCodeRequest request) throws UserNotFoundException {
        Referral createdCode = referralCodeService.createReferralCode(request);
        return ResponseEntity.ok(createdCode);
    }
   
    @ApiOperation(value = "Edit ReferralCode by ID")
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseMessage> editReferral(@PathVariable Long id, @RequestBody ReferralCodeRequest request)throws UserNotFoundException {
    
    	referralCodeService.updateReferralCode(id,request);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Referral updated Successfully"));
	}
    
    @ApiOperation(value = "Delete ReferralCode by ID")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseMessage> deleteReferral(@PathVariable Long id) throws UserNotFoundException {
        referralCodeService.deleteReferralCode(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessage("Referral deleted successfully"));
    }
    
	@ApiOperation(value = "get All Referrals")
	@RequestMapping(value = "/getAllReferral", method = RequestMethod.GET)
	public ResponseEntity<List<Referral>> getAllReferral()
			throws UserNotFoundException, InterruptedException {
		return ResponseEntity.status(HttpStatus.OK).body(referralCodeService.getAllReferrals());
	}
	
	@ApiOperation(value = "get All Referral By Id")
	@RequestMapping(value = "/getAllReferral/{id}", method = RequestMethod.GET)
	public ResponseEntity<Referral> getAllReferralById(@PathVariable Long id)
			throws UserNotFoundException, InterruptedException {
		return ResponseEntity.status(HttpStatus.OK).body(referralCodeService.findByReferralId(id));
	}
}
