package com.ewe.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.FleetDetailsForm;
import com.ewe.pojo.FleetDetails;
import com.ewe.pojo.Fleet_Vehicle;

public interface FleetService {

    void addFleet(FleetDetailsForm fleetDTO) throws UserNotFoundException, DuplicateUserException;

    FleetDetails getFleetById(Long id) throws UserNotFoundException;

    Map<String, Object> getAllFleets(int page, int size, String search)
            throws UserNotFoundException;

    void editFleet(Long id, FleetDetailsForm fleetForm) throws UserNotFoundException, DuplicateUserException;

    void addVehicleToFleet(Long fleetId, FleetDetailsForm vehicleForm) 

    throws UserNotFoundException, DuplicateUserException;

    List<Fleet_Vehicle> getFleetVehicles(Long fleetId) throws UserNotFoundException;

    Fleet_Vehicle getVehicleByNumber(String vehicleNumber) throws UserNotFoundException;

    void updateVehicle(String vehicleNumber, FleetDetailsForm vehicleForm) throws UserNotFoundException;

    void deleteVehicleFromFleet(Long fleetId, String vehicleNumber) throws UserNotFoundException;

	void deleteFleet(Long id) throws UserNotFoundException;

}