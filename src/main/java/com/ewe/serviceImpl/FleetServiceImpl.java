package com.ewe.serviceImpl;
	
	import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
	
	import org.springframework.beans.factory.annotation.Autowired;
	
	import org.springframework.stereotype.Service;
	
	import com.ewe.dao.GeneralDao;
	
	import com.ewe.exception.DuplicateUserException;
	
	import com.ewe.exception.UserNotFoundException;
	
	import com.ewe.form.FleetDetailsForm;
	
	import com.ewe.pojo.FleetDetails;
	
	import com.ewe.pojo.Fleet_Vehicle;
	
	import com.ewe.service.FleetService;
	
	@Service
	public class FleetServiceImpl implements FleetService {

	    @Autowired
	    private GeneralDao<?, ?> generalDao;	
	
	    @Override
	    public Fleet_Vehicle getVehicleByNumber(String vehicleNumber) throws UserNotFoundException {
	        String hql = "FROM Fleet_Vehicle v WHERE v.vehicleNumber = ?1";
	        List<Fleet_Vehicle> list = generalDao.findAllHQLQry(new Fleet_Vehicle(), hql, vehicleNumber);
	        
	        if (list == null || list.isEmpty()) {
	            throw new UserNotFoundException("Vehicle not found with number: " + vehicleNumber);
	        }
	        return list.get(0);
	    }
	    @Override
	    @Transactional
	    public void deleteVehicleFromFleet(Long fleetId, String vehicleNumber) throws UserNotFoundException {
	        String hql = "FROM Fleet_Vehicle v WHERE v.vehicleNumber = ?1";
	        List<Fleet_Vehicle> list = generalDao.findAllHQLQry(new Fleet_Vehicle(), hql, vehicleNumber);
	        
	        if (list == null || list.isEmpty()) {
	            throw new UserNotFoundException("Vehicle not found with number: " + vehicleNumber);
	        }
	        
	        Fleet_Vehicle vehicle = list.get(0);
	        if (vehicle.getFleetDetails() == null || !vehicle.getFleetDetails().getId().equals(fleetId)) {
	            throw new UserNotFoundException("Vehicle not found in the specified fleet");
	        }
	        generalDao.delete(vehicle);
	    }
	
	    @Override
	    @Transactional
	    public void addVehicleToFleet(Long fleetId, FleetDetailsForm vehicleForm) throws UserNotFoundException {
	        FleetDetails fleet = generalDao.findOneById(new FleetDetails(), fleetId);
	        if (fleet == null) {
	            throw new UserNotFoundException("Fleet not found with id: " + fleetId);
	        }
	        
	        Fleet_Vehicle vehicle = new Fleet_Vehicle();
	        // Change from setVehicleId to setVehicleNumber
	        vehicle.setVehicleNumber(vehicleForm.getVehicleNumber());
	        vehicle.setModel(vehicleForm.getModel());
	        vehicle.setDriver(vehicleForm.getDriver());
	        vehicle.setLocation(vehicleForm.getLocation());
	        vehicle.setCapacityKw(vehicleForm.getCapacityKw());
	        vehicle.setBookings(vehicleForm.getBookings());
	        vehicle.setBatteryLeft(vehicleForm.getBatteryLeft());
	        vehicle.setStatus(vehicleForm.getStatus());
	        vehicle.setFleetDetails(fleet);
	        generalDao.save(vehicle);
	    }
	    
	    @Override
	    public FleetDetails getFleetById(Long id) throws UserNotFoundException {
	        FleetDetails fleet = generalDao.findOneById(new FleetDetails(), id);
	        if (fleet == null) {	
	            throw new UserNotFoundException("Fleet not found with id: " + id);
	        }
	        return fleet;
	    }	
	
	    @Override
	    @Transactional
	    public void addFleet(FleetDetailsForm fleetForm)
	            throws UserNotFoundException, DuplicateUserException {

	        FleetDetails fleet = new FleetDetails();
	        fleet.setFleetName(fleetForm.getFleetName());
	        fleet.setOwnerName(fleetForm.getOwnerName());
	        fleet.setOwnerEmail(fleetForm.getOwnerEmail());
	        fleet.setOwnerPhone(fleetForm.getOwnerPhone());
	        fleet.setBaseLocation(fleetForm.getBaseLocation());
	        fleet.setStatus(fleetForm.getStatus());

	        generalDao.save(fleet);
	    }

	    @Override
	    @Transactional
	    public Map<String, Object> getAllFleets(int page, int size, String search)
	            throws UserNotFoundException {

	        int offset = page * size;

	        StringBuilder baseQuery = new StringBuilder(
	                "FROM fleet_details WHERE 1=1 "
	        );

	      

	        // Search - remember: SQL column names are snake_case
	        if (search != null && !search.trim().isEmpty()) {
	            String s = "%" + search.trim() + "%";

	            baseQuery.append(" AND (")
	                    .append("fleetName LIKE '").append(s).append("' OR ")
	                    .append("ownerName LIKE '").append(s).append("' OR ")
	                    .append("ownerEmail LIKE '").append(s).append("' OR ")
	                    .append("ownerPhone LIKE '").append(s).append("' OR ")
	                    .append("baseLocation LIKE '").append(s).append("' ")
	                    .append(")");
	        }

	        String countQuery = "SELECT COUNT(*) " + baseQuery.toString();
	        Long totalCount = generalDao.countSQL(countQuery);
	        String dataQuery =
	                "SELECT * " + baseQuery.toString() +
	                " ORDER BY id DESC " +
	                " OFFSET " + offset + " ROWS " +
	                " FETCH NEXT " + size + " ROWS ONLY";

	        List<FleetDetails> fleets =
	                generalDao.findAllSQLQuery(new FleetDetails(), dataQuery);
	        Map<String, Object> response = new HashMap<>();
	        response.put("content", fleets);
	        response.put("currentPage", page);
	        response.put("pageSize", size);
	        response.put("totalItems", totalCount);
	        response.put("totalPages", (int) Math.ceil((double) totalCount / size));

	        return response;
	    }
	    
	    @Override
	    @Transactional
	    public void editFleet(Long id, FleetDetailsForm fleetForm)
	            throws UserNotFoundException, DuplicateUserException {

	        FleetDetails fleet = generalDao.findOneById(new FleetDetails(), id);
	        if (fleet == null) {
	            throw new UserNotFoundException("Fleet not found with id: " + id);
	        }

	        if (fleetForm.getFleetName() != null) fleet.setFleetName(fleetForm.getFleetName());
	        if (fleetForm.getOwnerName() != null) fleet.setOwnerName(fleetForm.getOwnerName());
	        if (fleetForm.getBaseLocation() != null) fleet.setBaseLocation(fleetForm.getBaseLocation());
	        if (fleetForm.getStatus() != null) fleet.setStatus(fleetForm.getStatus());

	        generalDao.update(fleet);
	    }
	    
	    @Override
	    @Transactional
	    public void deleteFleet(Long id) throws UserNotFoundException {
	        FleetDetails fleet = generalDao.findOneById(new FleetDetails(), id);
	        if (fleet == null) {
	            throw new UserNotFoundException("Fleet not found with id: " + id);
	        }
	        generalDao.delete(fleet);
	    }
	    
	    @Override
	    @Transactional
	    public List<Fleet_Vehicle> getFleetVehicles(Long fleetId) throws UserNotFoundException {
	        FleetDetails fleet = generalDao.findOneById(new FleetDetails(), fleetId);
	        if (fleet == null) {
	            throw new UserNotFoundException("Fleet not found with id: " + fleetId);
	        }

	        String hql = "FROM Fleet_Vehicle v WHERE v.fleetDetails.id = ?1";
	        return generalDao.findAllHQLQry(new Fleet_Vehicle(), hql, fleetId);
	    }

	    @Override
	    @Transactional
	    public void updateVehicle(String vehicleNumber, FleetDetailsForm vehicleForm) throws UserNotFoundException {
	        String hql = "FROM Fleet_Vehicle v WHERE v.vehicleNumber = ?1";
	        List<Fleet_Vehicle> list = generalDao.findAllHQLQry(new Fleet_Vehicle(), hql, vehicleNumber);
	        
	        if (list == null || list.isEmpty()) {
	            throw new UserNotFoundException("Vehicle not found with number: " + vehicleNumber);
	        }
	        
	        Fleet_Vehicle vehicle = list.get(0);
	        
	        // Update fields - also update vehicle number if provided
	        if (vehicleForm.getModel() != null) vehicle.setModel(vehicleForm.getModel());
	        if (vehicleForm.getCapacityKw() != null) vehicle.setCapacityKw(vehicleForm.getCapacityKw());
	        if (vehicleForm.getDriver() != null) vehicle.setDriver(vehicleForm.getDriver());
	        if (vehicleForm.getLocation() != null) vehicle.setLocation(vehicleForm.getLocation());
	        if (vehicleForm.getBatteryLeft() != null) vehicle.setBatteryLeft(vehicleForm.getBatteryLeft());
	        if (vehicleForm.getBookings() != null) vehicle.setBookings(vehicleForm.getBookings());
	        if (vehicleForm.getStatus() != null) vehicle.setStatus(vehicleForm.getStatus());
	        
	        generalDao.savOrupdate(vehicle);
	    }
	}