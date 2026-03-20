package com.ewe.service;

import java.util.List;
import java.util.Map;

import com.ewe.controller.advice.ServerException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ReferralCodeRequest;
import com.ewe.form.VehicleForm;
import com.ewe.pojo.Referral;

public interface ReferralService {

	Referral createReferralCode(ReferralCodeRequest request) throws UserNotFoundException;
    

	void updateReferralCode(Long id, ReferralCodeRequest request) throws UserNotFoundException;

	Referral findByReferralId(Long id);

	void deleteReferralCode(Long id) throws UserNotFoundException;
	
	List<Referral> getAllReferrals();
	
}