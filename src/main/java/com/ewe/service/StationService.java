package com.ewe.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.EvStationDto.EvStation;
import com.ewe.form.FavSiteDTO;
import com.ewe.form.PortStatusResponse;
import com.ewe.form.RequestedFranchisesDTO;
import com.ewe.form.StationDetailsForm;
import com.ewe.pojo.Port;
import com.ewe.pojo.RequestedFranchises;
import com.ewe.pojo.Station;

public interface StationService {

	void addStation(StationDetailsForm siteForm) throws UserNotFoundException;

	void editStation(Long id, StationDetailsForm form) throws UserNotFoundException;

	Station getStationById(Long id) throws UserNotFoundException;

	List<Station> getAllStationsPaginated(Long orgId, String search);

	void deleteStationById(Long id) throws UserNotFoundException;

	void updateStationStatus(Long stationId, String stationStatus);

	List<Station> searchStations(String siteName, String stationStatus, String currentType, Long orgId, int page, int size);

	int countStations(String siteName, String stationStatus, String currentType, Long orgId);

	List<Station> getAllStationsPaginated(Long orgId);

	List<Station> getAllStations(Long orgId);

	List<Map<String, Object>> getAllSitesSummary();

	List<Map<String, Object>> getStations(Double latitude, Double longitude, String address, String from, String to,
			int radius, String connectorType, String status, String powerType, Integer minPower);

//void deleteStationAndCleanupSite(Long stationId) throws UserNotFoundException;
	// added for requesting franchises

//	RequestedFranchises saveRequestedFranchise(RequestedFranchises franchise) throws UserNotFoundException;

	Object getAllRequestedFranchises();

	List<Map<String, Object>> getAllRequestedFranchisesAsMap(String category) throws UserNotFoundException;

	RequestedFranchises getRequestedFranchiseById(Long id) throws UserNotFoundException;

	List<RequestedFranchises> getRequestedFranchisesByUserId(Long userId) throws UserNotFoundException;

	List<Map<String, Object>> getAllStationsJson(Double latitude, Double longitude, Double radiusKm, String nameFilter,
			String fromAddress, String toAddress, String search) throws IOException;

	Map<String, Object> getPendingRequestsCount();

	void verifyAndProcessEvStations(EvStation[] evStations);

	List<RequestedFranchises> saveRequestedFranchise(RequestedFranchisesDTO dto) throws UserNotFoundException;

	Port updatePortPrice(long portId, Double portPrice);

	byte[] downloadLogFile(String stationId, String filename) throws Exception;

	PortStatusResponse getPortStatusWithReservationAndPricing(Long portId, Date calculationDate);

	String getCurrentPortStatus(Long portId);

}
