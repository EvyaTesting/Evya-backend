package com.ewe.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.pojo.ChargingActivity;

@Transactional(propagation = Propagation.REQUIRED)
public interface DashboardService {

	public List<Map<String, Object>> getStats(int id, LocalDate startDate, LocalDate endDate);

	public List<Map<String, Object>> getRevenueGraph(int orgId, LocalDate startDate, LocalDate endDate);

	public List<Map<String, Object>> getSessionGraph(int orgId, LocalDate startDate, LocalDate endDate);

	public List<Map<String, Object>> getStationStats(int orgId, LocalDate startDate, LocalDate endDate);

	public List<Map<String, Object>> getPortStats(int orgId, LocalDate startDate, LocalDate endDate);
	
//	public List<Map<String, Object>> getEvyaStats(int id);
//
//	public List<Map<String, Object>> getFranchiseStats(int ownerId);
//	public List<Map<String, Object>> getWhitelabelStats(int wlOrgId);
	
    //List<ChargingActivity> getChargingActivitiesByStationId(Long stationId);

}