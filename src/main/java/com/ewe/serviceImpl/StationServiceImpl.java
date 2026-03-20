package com.ewe.serviceImpl;

import java.io.ByteArrayOutputStream;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ewe.controller.MobileAPIController;
import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.EvStationDto;
import com.ewe.form.FavSiteDTO;
import com.ewe.form.PortStatusResponse;
import com.ewe.form.RequestResponseDTO;
import com.ewe.form.RequestedFranchisesDTO;
import com.ewe.form.RequestedFranchisesDTO.SiteInfo;
import com.ewe.form.RequestedFranchisesDTO.StationInfo;
import com.ewe.form.SiteRequestsDTO;
import com.ewe.form.StationDetailsForm;
import com.ewe.form.StationRequestsDTO;
import com.ewe.pojo.ChargerDetails;
import com.ewe.pojo.Fav_Sites;
import com.ewe.pojo.Owner_Orgs;
import com.ewe.pojo.Password;
import com.ewe.pojo.Port;
import com.ewe.pojo.RequestedFranchises;
import com.ewe.pojo.Site;
import com.ewe.pojo.SiteLocationDetails;
import com.ewe.pojo.SiteOperationalDetails;
import com.ewe.pojo.Station;
import com.ewe.pojo.StatusNotification;
import com.ewe.pojo.User;
import com.ewe.pojo.Usersinroles;
import com.ewe.pojo.users_in_owners;
import com.ewe.service.StationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import io.jsonwebtoken.io.IOException;

@Service
@Transactional
public class StationServiceImpl implements StationService{
	@Autowired
	private MobileAPIController mobileApiController;
	 @Autowired
	 private GeneralDao<?, ?> generalDao;
	 @PersistenceContext
	   private EntityManager entityManager;
	 @Value("${google.maps.api.key}")
	    private String apiKey;

	    private final RestTemplate restTemplate;
	    private final ObjectMapper objectMapper;
	    
	    private static final Long WHITELABEL_ORG_ID = 298L;
	    private static final Long FRANCHISE_OWNER_ROLE_ID = 4L;
	    private static final Long DEFAULT_MANUFACTURER_ID = 169L;

	    public StationServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
	        this.restTemplate = restTemplate;
	        this.objectMapper = objectMapper;
	    }	    
	    
	 @Override
	 public void addStation(StationDetailsForm form) throws UserNotFoundException {

    Station station = new Station();

    station.setStationName(form.getStationName());
    station.setOCPPId(form.getOCPPId());
    station.setSerialNo(form.getSerialNo());
    station.setModel(form.getModel());
    station.setManufacturerId(form.getManufacturerId());
    station.setFirmware_version(form.getFirmware_version());
    station.setOcppVersion(form.getOcppVersion());
    station.setCommunication_method(form.getCommunication_method());
    station.setStationStatus(form.getStationStatus());
    station.setMax_output_power_kW(form.getMax_output_power_kW());
    station.setVoltage_range(form.getVoltage_range());
    station.setCurrent_type(form.getCurrent_type());
    station.setNumber_of_ports(form.getNumber_of_ports());
    station.setV2G_support(form.isV2G_support());
    station.setPlug_and_charger(form.isPlug_and_charger());
    station.setLastHeartBeat(form.getLastHeartBeat());

    if (form.getSiteId() > 0) {
        String query = "Select * from site where id = " + form.getSiteId();
        Site site = generalDao.findOneSQLQuery(new Site(), query);
        station.setSite(site);
    }

    if (form.getChargerdetailsId() > 0) {
        String query = "Select * from charger_details where id = " + form.getChargerdetailsId();
        ChargerDetails chargerDetails = generalDao.findOneSQLQuery(new ChargerDetails(), query);
        station.setChargerdetails(chargerDetails);
    }

    generalDao.save(station);

    if (form.getNumber_of_ports() >= 1) {
        Port port1 = new Port();
        port1.setConnectorId(1);
        port1.setConnectorName(form.getConnectorName1());
        port1.setConnector_type(form.getConnector_type1());
        port1.setPower_type(form.getPower_type1());
        port1.setVoltage_rating(form.getVoltage_rating1());
        port1.setCurrent_rating(form.getCurrent_rating1());
        port1.setMax_power_kW(form.getMax_power_kW1());
        port1.setBillingUnits(form.getBillingUnits1());
        port1.setBillingAmount(form.getBillingAmount1());
        port1.setStation(station);

        if (form.getConnectorDetailsID1() > 0) {
            String query = "Select * from connector_details where id = " + form.getConnectorDetailsID1();
            com.ewe.pojo.ConnectorDetails connectorDetails = generalDao.findOneSQLQuery(new com.ewe.pojo.ConnectorDetails(), query);
            port1.setConnectorDetails(connectorDetails);
        }
        generalDao.save(port1);

        StatusNotification statusNoti1 = new StatusNotification();
        statusNoti1.setConnectorId(1L);
        statusNoti1.setPort(port1);
        statusNoti1.setStationId(station.getId());
        statusNoti1.setStatus("Inoperative");
        generalDao.save(statusNoti1);
    }

    // === PORT 2 ===
    if (form.getNumber_of_ports() >= 2) {
        Port port2 = new Port();
        port2.setConnectorId(2); // Hardcoded
        port2.setConnectorName(form.getConnectorName2());
        port2.setConnector_type(form.getConnector_type2());
        port2.setPower_type(form.getPower_type2());
        port2.setVoltage_rating(form.getVoltage_rating2());
        port2.setCurrent_rating(form.getCurrent_rating2());
        port2.setMax_power_kW(form.getMax_power_kW2());
        port2.setBillingUnits(form.getBillingUnits2());
        port2.setBillingAmount(form.getBillingAmount2());
        port2.setStation(station);

        if (form.getConnectorDetailsID2() > 0) {
            String query = "Select * from connector_details where id = " + form.getConnectorDetailsID2();
            com.ewe.pojo.ConnectorDetails connectorDetails = generalDao.findOneSQLQuery(new com.ewe.pojo.ConnectorDetails(), query);
            port2.setConnectorDetails(connectorDetails);
        }

        generalDao.save(port2);

        StatusNotification statusNoti2 = new StatusNotification();
        statusNoti2.setPort(port2);
        statusNoti2.setStationId(station.getId());
        statusNoti2.setStatus("Inoperative");
        statusNoti2.setConnectorId(2L);
        
        generalDao.save(statusNoti2);
    } 
   }
	 
	@Override
	public Station getStationById(Long id) throws UserNotFoundException {
		String query = "Select * from station where id = " + id;
		Station net = generalDao.findOneSQLQuery(new Station(), query);
		System.out.println("netw :" + net);
		return net;
	}

	@Override
	public void editStation(Long id, StationDetailsForm form) throws UserNotFoundException {
		Station station = getStationById(id);
		if (station != null) {
			station.setStationName(form.getStationName());
	        station.setOCPPId(form.getOCPPId());
	        station.setSerialNo(form.getSerialNo());
	        station.setModel(form.getModel());
	        station.setManufacturerId(form.getManufacturerId());
	        station.setFirmware_version(form.getFirmware_version());
	        station.setOcppVersion(form.getOcppVersion());
	        station.setCommunication_method(form.getCommunication_method());
	        station.setStationStatus(form.getStationStatus());
	        station.setMax_output_power_kW(form.getMax_output_power_kW());
	        station.setVoltage_range(form.getVoltage_range());
	        station.setCurrent_type(form.getCurrent_type());
	        station.setNumber_of_ports(form.getNumber_of_ports());
	        station.setV2G_support(form.isV2G_support());
	        station.setPlug_and_charger(form.isPlug_and_charger());
	        station.setLastHeartBeat(form.getLastHeartBeat());
	        if (form.getSiteId() > 0) {
	        	String query = "Select * from site where id  = " + form.getSiteId();
	        	Site site = generalDao.findOneSQLQuery(new Site(), query);
	            station.setSite(site);
	        }
	        
	        if (form.getChargerdetailsId() > 0) {
	        	
	        	String query = "Select * from charger_details where id  = " + form.getChargerdetailsId();
	        	ChargerDetails chargerDetails = generalDao.findOneSQLQuery(new ChargerDetails(), query);
	            station.setChargerdetails(chargerDetails);
	        }
			generalDao.savOrupdate(station);
		}		
	}
	
	@Transactional
	@Override
	public void deleteStationById(Long id) throws UserNotFoundException {
	    try {
	        Station station = generalDao.findOneById(new Station(), id);
	        if (station == null) {
	            throw new UserNotFoundException("Station not found with id: " + id);
	        }
	        List<StatusNotification> statusNotifications = getStatusNotificationsByStationId(id);
	        validatePortsDeletable(statusNotifications);
	        breakAllRelationships(station, statusNotifications);
	        deleteStatusNotificationsAndPorts(statusNotifications);
	        generalDao.delete(station);
	        cleanupSiteIfEmpty(station.getId());
	    } catch (Exception e) {
	        throw new UserNotFoundException("Failed to delete station: " + e.getMessage(), e);
	    }
	}

	private void breakAllRelationships(Station station, List<StatusNotification> statusNotifications) throws UserNotFoundException {
	    if (station.getSite() != null) {
	        Site site = station.getSite();
	        site.getStation().remove(station);
	        station.setSite(null);
	        generalDao.update(site);
	    }
	    for (StatusNotification notification : statusNotifications) {
	        if (notification.getPort() != null) {
	            Port port = notification.getPort();
	            port.setStatusNotifcation(null);
	            notification.setPort(null);
	            generalDao.update(port);
	            generalDao.update(notification);
	        }
	    }
	}

	private List<StatusNotification> getStatusNotificationsByStationId(Long stationId) {
	    StatusNotification template = new StatusNotification();
	    template.setStationId(stationId);
	    return generalDao.findAll(template);
	}

	private void validatePortsDeletable(List<StatusNotification> statusNotifications) {
	    if (statusNotifications == null || statusNotifications.isEmpty()) {
	        return;
	    }

	    List<String> operationalPorts = statusNotifications.stream()
	        .filter(notification -> notification != null 
	                            && notification.getPort() != null
	                            && notification.getStatus() != null
	                            && !"Inoperative".equalsIgnoreCase(notification.getStatus()) 
	                            && !"Blocked".equalsIgnoreCase(notification.getStatus()))
	        .map(notification -> "Port ID: " + notification.getPort().getId() + 
	                           " Status: " + notification.getStatus())
	        .collect(Collectors.toList());

	    if (!operationalPorts.isEmpty()) {
	        throw new IllegalStateException(
	            "Cannot delete station - it has operational ports: " + 
	            String.join(", ", operationalPorts)
	        );
	    }
	}

	private void deleteStatusNotificationsAndPorts(List<StatusNotification> statusNotifications) 
	    throws UserNotFoundException {
	    if (statusNotifications == null || statusNotifications.isEmpty()) {
	        return;
	    }
	    List<Long> portIds = statusNotifications.stream()
	        .filter(notification -> notification.getPort() != null)
	        .map(notification -> notification.getPort().getId())
	        .collect(Collectors.toList());

	    String notificationIds = statusNotifications.stream()
	        .map(notification -> notification.getId().toString())
	        .collect(Collectors.joining(","));
	    
	    generalDao.deleteSqlQuiries(
	        "DELETE FROM statusNotification WHERE id IN (" + notificationIds + ")");

	    if (!portIds.isEmpty()) {
	        String portIdsStr = portIds.stream()
	            .map(Object::toString)
	            .collect(Collectors.joining(","));
	        
	        generalDao.deleteSqlQuiries(
	            "DELETE FROM port WHERE id IN (" + portIdsStr + ")");
	    }
	}

	private void cleanupSiteIfEmpty(Long siteId) throws UserNotFoundException {
	    if (siteId == null) {
	        return;
	    }
	    String countSql = "SELECT COUNT(*) FROM station WHERE site_id = " + siteId;
	    Long stationCount = generalDao.countSQL(countSql);
	    
	    if (stationCount == 0) {
	        generalDao.deleteSqlQuiries("DELETE FROM site WHERE id = " + siteId);
	    }
	}

	@Override
	public List<Station> getAllStationsPaginated(Long orgId) {
	    return getAllStationsPaginated(orgId, null); 
	}	
	
	@Override
	public List<Station> getAllStationsPaginated(Long orgId, String search) {
    String query;

    if (orgId == null || orgId == 1) {
        if (search == null || search.trim().isEmpty()) {
            // Return all stations as a set to remove duplicates
            return new ArrayList<>(new LinkedHashSet<>(generalDao.findAll(new Station())));
        } else {
            String searchTerm = "%" + search.toLowerCase() + "%";
            query = "SELECT st.* FROM station st " +
                    "WHERE LOWER(st.stationName) LIKE '" + searchTerm + "' " +
                    "OR LOWER(st.OCPPId) LIKE '" + searchTerm + "' " +
                    "OR LOWER(st.serialNo) LIKE '"	 + searchTerm + "'";
            try {
                List<Station> stations = generalDao.findAllSQLQuery(new Station(), query);
                return new ArrayList<>(new LinkedHashSet<>(stations));
            } catch (UserNotFoundException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
    } else {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? "%" + search.toLowerCase() + "%" : null;

        query = "SELECT st.* FROM station st " +
                "LEFT JOIN site s ON st.site_id = s.id " +
                "LEFT JOIN owner_orgs oo ON s.ownerOrg = oo.id " +
                "WHERE (oo.whitelabelId = " + orgId + " OR s.ownerOrg = " + orgId + ")";

        if (searchTerm != null) {
            query += " AND (LOWER(st.station_name) LIKE '" + searchTerm + "' " +
                    "OR LOWER(st.location) LIKE '" + searchTerm + "' " +
                    "OR LOWER(st.description) LIKE '" + searchTerm + "')";
        }

        try {
            List<Station> stations = generalDao.findAllSQLQuery(new Station(), query);
            // Use Set to remove duplicates
            Set<Station> uniqueStations = new LinkedHashSet<>(stations);
            return new ArrayList<>(uniqueStations);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

	@Override
    @Transactional
    public void updateStationStatus(Long stationId, String stationStatus) {
        String query = "UPDATE station SET stationStatus = '" + stationStatus + "' WHERE id = " + stationId;
        int updated = generalDao.updateSqlQuery(query);

        if (updated == 0) {
            throw new RuntimeException("Station not found or update failed for ID: " + stationId);
        }
	}

	@Override
	public List<Station> searchStations(String siteName, String stationStatus, String currentType, Long orgId, int page, int size) {
	   StringBuilder hql = new StringBuilder("FROM Station s WHERE 1=1 ");
	   Map<String, Object> params = new HashMap<>();
	   
	   if (orgId != null && orgId != 1) { 
	       hql.append("AND (s.site.ownerOrg = :orgId OR s.site.ownerOrg IN " +
	                  "(SELECT oo.id FROM Owner_Orgs oo WHERE oo.whitelabelId = :orgId)) ");
	       params.put("orgId", orgId);
	   }
	
	   if (siteName != null && !siteName.isBlank()) {
	       hql.append("AND LOWER(s.site.siteName) LIKE LOWER(CONCAT('%', :siteName, '%')) ");
	       params.put("siteName", siteName);
	   }
	
	   if (stationStatus != null && !stationStatus.isBlank()) {
	       hql.append("AND LOWER(s.stationStatus) = LOWER(:stationStatus) ");
	       params.put("stationStatus", stationStatus);
	   }
	
	   if (currentType != null && !currentType.isBlank()) {
	       hql.append("AND LOWER(s.current_type) = LOWER(:currentType) ");
	       params.put("currentType", currentType);
	   }
	
	   hql.append("ORDER BY s.id DESC");
	   return generalDao.findAllHQLQueryPaginated(new Station(), hql.toString(), page, size, params);
	}
	
	@Override
	public int countStations(String siteName, String stationStatus, String currentType, Long orgId) {
	   StringBuilder hql = new StringBuilder("SELECT COUNT(s.id) FROM Station s WHERE 1=1 ");
	   Map<String, Object> params = new HashMap<>();
	
	   if (siteName != null && !siteName.isBlank()) {
	       hql.append("AND LOWER(s.site.siteName) LIKE LOWER(CONCAT('%', :siteName, '%')) ");
	       params.put("siteName", siteName);
	   }
	
	   if (stationStatus != null && !stationStatus.isBlank()) {
	       hql.append("AND LOWER(s.stationStatus) = LOWER(:stationStatus) ");
	       params.put("stationStatus", stationStatus);
	   }
	
	   if (currentType != null && !currentType.isBlank()) {
	       hql.append("AND LOWER(s.current_type) = LOWER(:currentType) ");
	       params.put("currentType", currentType);
	   }
	
	   return generalDao.countHQLQuery(hql.toString(), params).intValue();
	}

	@Override
	public List<Station> getAllStations(Long orgId) {
	    try {
	        return generalDao.findAll(new Station());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAllSitesSummary() {
	    // Fetch all sites with EAGER station and port
	    List<Site> sites = entityManager.createQuery(
	        "SELECT DISTINCT s FROM Site s LEFT JOIN FETCH s.station st LEFT JOIN FETCH st.port p LEFT JOIN FETCH p.statusNotifcation", 
	        Site.class
	    ).getResultList();
	    
	    List<Map<String, Object>> result = new ArrayList<>();
	    
	    for (Site site : sites) {
	        // Calculate aggregates for this site
	        List<Double> sitePowers = new ArrayList<>();
	        List<Double> sitePrices = new ArrayList<>();
	        Set<String> sitePowerTypes = new HashSet<>();
	        int totalPorts = 0;
	        int availablePorts = 0;

	        // First, calculate all aggregates
	        for (Station station : site.getStation()) {
	            for (Port port : station.getPort()) {
	                totalPorts++;
	                
	                if (port.getMax_power_kW() != null) {
	                    sitePowers.add(port.getMax_power_kW());
	                }
	                if (port.getBillingAmount() != null) {
	                    sitePrices.add(port.getBillingAmount());
	                }
	                if (port.getPower_type() != null) {
	                    sitePowerTypes.add(port.getPower_type().toUpperCase());
	                }
	                
	                // Check status for available ports
	                for (StatusNotification status : port.getStatusNotifcation()) {
	                    if ("Available".equalsIgnoreCase(status.getStatus())) {
	                        availablePorts++;
	                    }
	                }
	            }
	        }
	        
	        // Create siteMap with LinkedHashMap to maintain order
	        Map<String, Object> siteMap = new LinkedHashMap<>();
	        
	        // Add fields in the EXACT order you want:
	        siteMap.put("totalPorts", totalPorts);
	        siteMap.put("totalStations", site.getStation().size());
	        siteMap.put("availablePorts", availablePorts);
	        if (!sitePowers.isEmpty()) {
	            Double minSitePower = Collections.min(sitePowers);
	            Double maxSitePower = Collections.max(sitePowers);
	            siteMap.put("power", String.format("%.0f-%.0f", minSitePower, maxSitePower));
	        }
	        if (!sitePrices.isEmpty()) {
	            Double minSitePrice = Collections.min(sitePrices);
	            Double maxSitePrice = Collections.max(sitePrices);
	            siteMap.put("price", String.format("%.0f-%.0f", minSitePrice, maxSitePrice));
	        }
	        if (!sitePowerTypes.isEmpty()) {
	            siteMap.put("power type", String.join(", ", sitePowerTypes));
	        }
	        siteMap.put("siteId", site.getId());
	        siteMap.put("siteName", site.getSiteName());
	        siteMap.put("ownerOrgId", site.getOwnerOrg());
	        
	        List<Map<String, Object>> locations = new ArrayList<>();
	        site.getLocation().forEach(loc -> {
	            Map<String, Object> locMap = new HashMap<>();
	            locMap.put("address", loc.getAddress());
	            locMap.put("latitude", loc.getLatitude());
	            locMap.put("longitude", loc.getLongitude());
	            locations.add(locMap);
	        });
	        siteMap.put("locations", locations);
	        List<Map<String, Object>> stationsList = new ArrayList<>();
	        for (Station station : site.getStation()) {
	            Map<String, Object> stationMap = new HashMap<>();
	            stationMap.put("stationId", station.getId());
	            stationMap.put("stationName", station.getStationName());
	            stationMap.put("current Type", station.getCurrent_type());
	            stationMap.put("last heartbeat", station.getLastHeartBeat());
	            stationMap.put("status", station.getStationStatus());
	            stationMap.put("serial number", station.getSerialNo());

	            List<Map<String, Object>> portsList = new ArrayList<>();
	            for (Port port : station.getPort()) {
	                Map<String, Object> portMap = new HashMap<>();
	                portMap.put("portId", port.getId());
	                portMap.put("connectorId", port.getConnectorId());
	                portMap.put("connectorName", port.getConnectorName());
	                portMap.put("power capacity", port.getMax_power_kW());
	                portMap.put("billing units", port.getBillingUnits());
	                portMap.put("connectorType", port.getConnector_type());
	                portMap.put("powerType", port.getPower_type());
	                portMap.put("billingAmount", port.getBillingAmount());

	                List<Map<String, Object>> statusList = new ArrayList<>();
	                for (StatusNotification status : port.getStatusNotifcation()) {
	                    Map<String, Object> statusMap = new HashMap<>();
	                    statusMap.put("statusId", status.getId());
	                    statusMap.put("status", status.getStatus());
	                    statusMap.put("lastContactedTime", status.getLastContactedTime());
	                    statusList.add(statusMap);
	                }
	                portMap.put("statusNotifications", statusList);
	                portsList.add(portMap);
	            }
	            stationMap.put("ports", portsList);
	            stationsList.add(stationMap);
	        }
	        siteMap.put("stations", stationsList);
	        siteMap.put("ownerId", site.getOwnerId());

	        result.add(siteMap);
	    }
	    return result;
	}

	@Override
	public List<Map<String, Object>> getStations(
	        Double latitude,
	        Double longitude,
	        String address,
	        String from,
	        String to,
	        int radius,
	        String connectorType,
	        String status,
	        String powerType,
	        Integer minPower) {

	    List<Map<String, Object>> stations = new ArrayList<>();

	    try {
	        if (from != null && to != null) {
	            stations = fetchStationsBetweenAddresses(from, to, connectorType, status, powerType, minPower);

	        } else if (address != null) {
	            double[] latLng = geocodeAddress(address);
	            stations = fetchNearbyStations(latLng[0], latLng[1], radius);

	        } else if (latitude != null && longitude != null) {
	            stations = fetchNearbyStations(latitude, longitude, radius);

	        } else {
	            return Collections.emptyList();
	        }
	        return applyFilters(stations, connectorType, status, powerType, minPower);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}


	private List<Map<String, Object>> fetchNearbyStations(double lat, double lng, int radius) throws Exception {
	    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
	            "location=" + lat + "," + lng +
	            "&radius=" + radius +
	            "&type=charging_station" +
	            "&keyword=EV%20charging" +
	            "&key=" + apiKey;

	    return fetchAndMapResults(url);
	}

	private List<Map<String, Object>> fetchStationsBetweenAddresses(
	        String from, String to, String connectorType, String status, String powerType, Integer minPower) throws Exception {

	    double[] fromLatLng = geocodeAddress(from);
	    double[] toLatLng = geocodeAddress(to);

	    String directionsUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
	            "origin=" + fromLatLng[0] + "," + fromLatLng[1] +
	            "&destination=" + toLatLng[0] + "," + toLatLng[1] +
	            "&key=" + apiKey;

	    String response = restTemplate.getForObject(directionsUrl, String.class);
	    JsonNode root = objectMapper.readTree(response);

	    List<Map<String, Object>> routeStations = new ArrayList<>();
	    JsonNode steps = root.path("routes").get(0).path("legs").get(0).path("steps");

	    for (JsonNode step : steps) {
	        double lat = step.path("end_location").path("lat").asDouble();
	        double lng = step.path("end_location").path("lng").asDouble();
	        routeStations.addAll(fetchNearbyStations(lat, lng, 3000)); // 3 km buffer
	    }
	    return routeStations;
	}

	private List<Map<String, Object>> fetchAndMapResults(String url) throws Exception {
	    String response = restTemplate.getForObject(url, String.class);
	    JsonNode root = objectMapper.readTree(response);
	    JsonNode results = root.path("results");

	    List<Map<String, Object>> sites = new ArrayList<>();
	    for (JsonNode place : results) {
	        Map<String, Object> site = new HashMap<>();
	        String placeId = place.path("place_id").asText();

	        Map<String, String> placeDetails = fetchPlaceDetails(placeId);

	        site.put("siteName", place.path("name").asText());
	        site.put("siteId", UUID.randomUUID().toString());
	        site.put("managerName", place.path("name").asText());
	        site.put("managerEmail", placeDetails.getOrDefault("email", "info@default.com"));
	        site.put("managerPhone", placeDetails.getOrDefault("phone", "9999999999"));

	        Map<String, Object> location = new HashMap<>();
	        location.put("address", place.path("vicinity").asText(""));
	        location.put("latitude", place.path("geometry").path("location").path("lat").asDouble());
	        location.put("longitude", place.path("geometry").path("location").path("lng").asDouble());
	        site.put("locations", Collections.singletonList(location));

	        int totalStations = 1 + new Random().nextInt(3);
	        int totalPorts = 1 + new Random().nextInt(4);
	        site.put("totalStations", totalStations);
	        site.put("totalPorts", totalPorts);
	        site.put("availablePorts", Math.max(0, totalPorts - new Random().nextInt(totalPorts)));

	        List<Map<String, Object>> stationList = new ArrayList<>();
	        for (int i = 0; i < totalStations; i++) {
	            Map<String, Object> station = new HashMap<>();
	            station.put("stationId", UUID.randomUUID().toString());
	            station.put("stationName", site.get("siteName") + " Station " + (i + 1));

	            List<Map<String, Object>> ports = new ArrayList<>();
	            for (int j = 0; j < totalPorts; j++) {
	                boolean isDC = new Random().nextBoolean();
	                Map<String, Object> port = new HashMap<>();
	                port.put("connectorType", isDC ? "CCS2" : "Type2");
	                port.put("powerType", isDC ? "DC" : "AC");
	                port.put("powerCapacity", isDC ? 50 + new Random().nextInt(251) : 7 + new Random().nextInt(16));
	                port.put("connectorId", j + 1);
	                port.put("connectorName", "Port" + (j + 1));
	                port.put("portId", UUID.randomUUID().toString());
	                port.put("billingUnits", "kWh");
	                port.put("billingAmount", isDC ? 20 + new Random().nextInt(31) : 5 + new Random().nextInt(16));

	                Map<String, Object> status = new HashMap<>();
	                status.put("status", new Random().nextBoolean() ? "Available" : "Occupied");
	                status.put("lastContactedTime", null);
	                port.put("statusNotifications", Collections.singletonList(status));

	                ports.add(port);
	            }
	            station.put("ports", ports);
	            stationList.add(station);
	        }
	        site.put("stations", stationList);
	        sites.add(site);
	    }
	    return sites;
	}

	private Map<String, String> fetchPlaceDetails(String placeId) throws Exception {
	    Map<String, String> details = new HashMap<>();
	    String detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?" +
	            "place_id=" + placeId +
	            "&fields=formatted_phone_number,website" +
	            "&key=" + apiKey;

	    String response = restTemplate.getForObject(detailsUrl, String.class);
	    JsonNode root = objectMapper.readTree(response);
	    JsonNode result = root.path("result");

	    details.put("phone", result.path("formatted_phone_number").asText("9999999999"));
	    if (!result.path("website").asText("").isEmpty()) {
	        String website = result.path("website").asText();
	        String domain = website.replaceAll("https?://(www\\.)?", "").split("/")[0];
	        details.put("email", "info@" + domain);
	    }
	    return details;
	}

	private double[] geocodeAddress(String address) throws Exception {
	    String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
	            address.replace(" ", "+") + "&key=" + apiKey;
	    String response = restTemplate.getForObject(geocodeUrl, String.class);
	    JsonNode root = objectMapper.readTree(response);
	    JsonNode location = root.path("results").get(0).path("geometry").path("location");
	    return new double[]{location.path("lat").asDouble(), location.path("lng").asDouble()};
	}

	private List<Map<String, Object>> applyFilters(List<Map<String, Object>> sites,
	                                              String connectorType,
	                                              String status,
	                                              String powerType,
	                                              Integer minPower) {
	    return sites.stream()
	            .map(site -> {
	                List<Map<String, Object>> filteredStations = ((List<Map<String, Object>>) site.get("stations")).stream()
	                        .map(station -> {
	                            List<Map<String, Object>> filteredPorts = ((List<Map<String, Object>>) station.get("ports")).stream()
	                                    .filter(port -> connectorType == null || port.get("connectorType").toString().equalsIgnoreCase(connectorType))
	                                    .filter(port -> powerType == null || port.get("powerType").toString().equalsIgnoreCase(powerType))
	                                    .filter(port -> minPower == null || ((Number) port.get("powerCapacity")).intValue() >= minPower)
	                                    .filter(port -> status == null || ((List<Map<String, Object>>) port.get("statusNotifications")).get(0).get("status").equals(status))
	                                    .collect(Collectors.toList());

	                            if (!filteredPorts.isEmpty()) {
	                                station.put("ports", filteredPorts);
	                                return station;
	                            }
	                            return null;
	                        }).filter(Objects::nonNull).collect(Collectors.toList());

	                if (!filteredStations.isEmpty()) {
	                    site.put("stations", filteredStations);
	                    return site;
	                }
	                return null;
	            }).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public List<RequestedFranchises> saveRequestedFranchise(RequestedFranchisesDTO dto) throws UserNotFoundException {

	    if (dto.getCategory() == null || dto.getCategory().isEmpty()) {
	        throw new IllegalArgumentException("Category is required: FRANCHISE, SITE, or STATION");
	    }
	    
	    User user = generalDao.findOneById(new User(), dto.getUserId());
	    if (user == null) {
	        throw new UserNotFoundException("User not found with ID: " + dto.getUserId());
	    }

	    String category = dto.getCategory().toUpperCase();
	    List<RequestedFranchises> savedList = new ArrayList<>();

	    if (!List.of("FRANCHISE", "SITE", "STATION").contains(category)) {
	        throw new IllegalArgumentException("Invalid category: " + category);
	    }
	    
	    if ("SITE".equalsIgnoreCase(dto.getCategory())) {
	        if (dto.getOwnerOrgId() == null) {
	            throw new IllegalArgumentException("Franchise ID is required when requesting a SITE");
	        }
	        Owner_Orgs franchise = generalDao.findOneById(new Owner_Orgs(), dto.getOwnerOrgId());
	        if (franchise == null ) {
	            throw new IllegalArgumentException("Invalid Franchise ID. Franchise does not exist.");
	        }
	    }

	    if ("STATION".equalsIgnoreCase(dto.getCategory())) {
	        if (dto.getOwnerOrgId() == null || dto.getSiteId() == null) {
	            throw new IllegalArgumentException("Franchise ID and Site ID are required when requesting a STATION");
	        }

	        Owner_Orgs franchise = generalDao.findOneById(new Owner_Orgs(), dto.getOwnerOrgId());
	        if (franchise == null) {
	            throw new IllegalArgumentException("Invalid Franchise ID. Franchise does not exist.");
	        }
	        
	        Site site = generalDao.findOneById(new Site(), dto.getSiteId());
	        if (site == null) {
	            throw new IllegalArgumentException("Invalid Site ID. Site does not exist.");
	        }
	        
	        if (site.getOwnerOrg() != franchise.getId()) {
	            throw new IllegalArgumentException(String.format(
	                "Invalid hierarchy: Site ID %d does not belong to Franchise ID %d",
	                site.getId(), franchise.getId()
	            ));
	        }
	        }

	    Owner_Orgs owner = null;
	    if (dto.getOwnerOrgId() != null) {
	        owner = generalDao.findOneById(new Owner_Orgs(), dto.getOwnerOrgId());
	        if (owner == null) throw new UserNotFoundException("Owner Org not found with ID: " + dto.getOwnerOrgId());
	    }

	    if ("FRANCHISE".equals(category)) {

	        if (dto.getSites() == null || dto.getSites().isEmpty()) {
	            throw new IllegalArgumentException("Franchise request must include sites");
	        }

	        for (RequestedFranchisesDTO.SiteInfo siteDTO : dto.getSites()) {
	            if (siteDTO.getStations() == null || siteDTO.getStations().isEmpty()) {
	                throw new IllegalArgumentException("Each site must include at least one station");
	            }

	            for (RequestedFranchisesDTO.StationInfo stDTO : siteDTO.getStations()) {

	                RequestedFranchises f = new RequestedFranchises();
	                f.setCategory("FRANCHISE");
	                f.setFranchiseName(dto.getFranchiseName());
	                f.setSitename(siteDTO.getSitename());
	                f.setStationName(stDTO.getStationName());
	                f.setAddress(dto.getAddress());
	                f.setLatitude(dto.getLatitude());
	                f.setLongitude(dto.getLongitude());
	                f.setMobileNumber(dto.getMobileNumber());
	                f.setEmail(dto.getEmail());
	                f.setStatus(false);
	                f.setRequestedBy(user);
	                if (owner != null) f.setOwner_orgs(owner);
	                generalDao.savOrupdate(f);
	                savedList.add(f);

	                RequestedFranchises s = new RequestedFranchises();
	                s.setCategory("SITE");
	                s.setFranchiseName(dto.getFranchiseName());
	                s.setSitename(siteDTO.getSitename());
	                s.setStationName(stDTO.getStationName());
	                s.setAddress(siteDTO.getAddress());
	                s.setLatitude(siteDTO.getLatitude());
	                s.setLongitude(siteDTO.getLongitude());
	                s.setMobileNumber(dto.getMobileNumber());
	                s.setEmail(dto.getEmail());
	                s.setStatus(false);
	                s.setRequestedBy(user);
	                if (owner != null) s.setOwner_orgs(owner);
	                generalDao.savOrupdate(s);
	                savedList.add(s);

	                RequestedFranchises st = new RequestedFranchises();
	                st.setCategory("STATION");
	                st.setFranchiseName(dto.getFranchiseName());
	                st.setSitename(siteDTO.getSitename());
	                st.setStationName(stDTO.getStationName());
	                st.setAddress(siteDTO.getAddress());
	                st.setLatitude(siteDTO.getLatitude());
	                st.setLongitude(siteDTO.getLongitude());
	                st.setMobileNumber(dto.getMobileNumber());
	                st.setEmail(dto.getEmail());
	                st.setStatus(false);
	                st.setRequestedBy(user);
	                if (owner != null) st.setOwner_orgs(owner);
	                generalDao.savOrupdate(st);
	                savedList.add(st);
	            }
	        }
	    }

	    else if ("SITE".equals(category)) {

	        if (dto.getSites() == null || dto.getSites().isEmpty()) {
	            throw new IllegalArgumentException("Site request must include at least one site");
	        }

	        for (RequestedFranchisesDTO.SiteInfo siteDTO : dto.getSites()) {
	            if (siteDTO.getStations() == null || siteDTO.getStations().isEmpty()) {
	                throw new IllegalArgumentException("Each site must include at least one station");
	            }

	            for (RequestedFranchisesDTO.StationInfo stDTO : siteDTO.getStations()) {

	                RequestedFranchises s = new RequestedFranchises();
	                s.setCategory("SITE");
	                s.setFranchiseName(dto.getFranchiseName());
	                s.setSitename(siteDTO.getSitename());
	                s.setStationName(stDTO.getStationName());
	                s.setAddress(siteDTO.getAddress());
	                s.setLatitude(siteDTO.getLatitude());
	                s.setLongitude(siteDTO.getLongitude());
	                s.setMobileNumber(dto.getMobileNumber());
	                s.setEmail(dto.getEmail());
	                s.setStatus(false);
	                s.setRequestedBy(user);
	                if (owner != null) s.setOwner_orgs(owner);
	                generalDao.savOrupdate(s);
	                savedList.add(s);

	                RequestedFranchises stn = new RequestedFranchises();
	                stn.setCategory("STATION");
	                stn.setFranchiseName(dto.getFranchiseName());
	                stn.setSitename(siteDTO.getSitename());
	                stn.setStationName(stDTO.getStationName());
	                stn.setAddress(siteDTO.getAddress());
	                stn.setLatitude(siteDTO.getLatitude());
	                stn.setLongitude(siteDTO.getLongitude());
	                stn.setMobileNumber(dto.getMobileNumber());
	                stn.setEmail(dto.getEmail());
	                stn.setStatus(false);
	                stn.setRequestedBy(user);
	                if (owner != null) stn.setOwner_orgs(owner);
	                generalDao.savOrupdate(stn);
	                savedList.add(stn);
	            }
	        }
	    }

	    else if ("STATION".equals(category)) {

	        if (dto.getSites() == null || dto.getSites().isEmpty()) {
	            throw new IllegalArgumentException("At least one site must be provided for a station request");
	        }

	        for (RequestedFranchisesDTO.SiteInfo siteDTO : dto.getSites()) {
	            if (siteDTO.getStations() == null || siteDTO.getStations().isEmpty()) {
	                throw new IllegalArgumentException("Each site must include at least one station");
	            }

	            for (RequestedFranchisesDTO.StationInfo stDTO : siteDTO.getStations()) {

	                RequestedFranchises stn = new RequestedFranchises();
	                stn.setCategory("STATION");
	                stn.setFranchiseName(dto.getFranchiseName());
	                stn.setSitename(siteDTO.getSitename());
	                stn.setStationName(stDTO.getStationName());
	                stn.setAddress(dto.getAddress());
	                stn.setLatitude(dto.getLatitude());
	                stn.setLongitude(dto.getLongitude());
	                stn.setMobileNumber(dto.getMobileNumber());
	                stn.setEmail(dto.getEmail());
	                stn.setStatus(false);
	                stn.setRequestedBy(user);

	                if (owner != null) stn.setOwner_orgs(owner);
	                
	                if (dto.getSiteId() != null) {
	                    Site site = generalDao.findOneById(new Site(), dto.getSiteId());
	                    if (site != null) {
	                        stn.setSite(site);
	                    } else {
	                        throw new IllegalArgumentException("Invalid Site ID: " + dto.getSiteId());
	                    }
	                } else {
	                    throw new IllegalArgumentException("Site ID is required for station request");
	                }

	                generalDao.savOrupdate(stn);
	                savedList.add(stn);
	            }
	        }
	    }

	    return savedList;
	}
	
	private RequestResponseDTO mapToResponseDTO(RequestedFranchisesDTO dto, RequestedFranchises entity) {
	    RequestResponseDTO response = new RequestResponseDTO();
	    response.setId(entity != null ? entity.getId() : null);
	    response.setCategory(dto.getCategory());
	    response.setFranchiseName(dto.getFranchiseName());
	    response.setAddress(dto.getAddress());
	    response.setLatitude(dto.getLatitude() != null ? String.valueOf(dto.getLatitude()) : null);
	    response.setLongitude(dto.getLongitude() != null ? String.valueOf(dto.getLongitude()) : null);
	    response.setMobileNumber(dto.getMobileNumber());
	    response.setEmail(dto.getEmail());
	    response.setStatus(entity != null ? entity.isStatus() : false);

	    if (dto.getSites() != null && !dto.getSites().isEmpty()) {
	        List<RequestResponseDTO.SiteInfo> siteList = dto.getSites().stream().map(siteDTO -> {
	            RequestResponseDTO.SiteInfo siteInfo = new RequestResponseDTO.SiteInfo();
	            siteInfo.setSitename(siteDTO.getSitename());
	            siteInfo.setAddress(siteDTO.getAddress());
	            siteInfo.setLatitude(siteDTO.getLatitude() != null ? String.valueOf(siteDTO.getLatitude()) : null);
	            siteInfo.setLongitude(siteDTO.getLongitude() != null ? String.valueOf(siteDTO.getLongitude()) : null);

	            if (siteDTO.getStations() != null && !siteDTO.getStations().isEmpty()) {
	                List<RequestResponseDTO.StationInfo> stationList = siteDTO.getStations().stream().map(stDTO -> {
	                    RequestResponseDTO.StationInfo stInfo = new RequestResponseDTO.StationInfo();
	                    stInfo.setStationName(stDTO.getStationName());
	                    stInfo.setChargerCapacity(stDTO.getChargerCapacity());
	                    return stInfo;
	                }).toList();
	                siteInfo.setStations(stationList);
	            }
	            return siteInfo;
	        }).toList();

	        response.setSites(siteList);
	    }
	    return response;
	}
	
	@Override
    public List<RequestedFranchises> getAllRequestedFranchises() {
        return generalDao.findAll(new RequestedFranchises());
    }
	
    @Override
    public List<Map<String, Object>> getAllRequestedFranchisesAsMap(String category) throws UserNotFoundException {
        String baseQuery = "SELECT rf.id, rf.category, rf.franchiseName, rf.sitename, rf.stationName, " +
                           "rf.address, rf.latitude, rf.longitude, rf.chargerCapacity, rf.mobileNumber, rf.email " +
                           "FROM requested_franchises rf WHERE 1=1";
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            baseQuery += " AND UPPER(rf.category) = '" + category.toUpperCase() + "'";
        }
        return generalDao.getMapData(baseQuery);
    }
    
    @Override
    public RequestedFranchises getRequestedFranchiseById(Long id) throws UserNotFoundException {
        RequestedFranchises request = (RequestedFranchises) generalDao.findOneById(new RequestedFranchises(), id);
        if(request == null) {
            throw new UserNotFoundException("Requested Franchise with id " + id + " not found");
        }
        return request;
    }
    
    @Override
    public List<RequestedFranchises> getRequestedFranchisesByUserId(Long userId) throws UserNotFoundException {
        String query = "SELECT * FROM requested_franchises WHERE user_id = " + userId;
        List<RequestedFranchises> list = generalDao.findAllSQLQuery(new RequestedFranchises(), query);
        if (list == null || list.isEmpty()) {
            throw new UserNotFoundException("No requests found for user with ID: " + userId);
        }
        return list;
    }
    
    @Override
	public List<Map<String, Object>> getAllStationsJson(
	        Double latitude,
	        Double longitude,
	        Double radius,
	        String nameFilter,
	        String from,
	        String to,
	        String search) throws java.io.IOException {

	    List<Map<String, Object>> result = new ArrayList<>();
	    double effectiveRadius = (radius != null && radius > 0) ? radius : 15.0;

	    try (InputStream inputStream = new ClassPathResource("data/ev_stations.json").getInputStream()) {
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode rootNode = mapper.readTree(inputStream);
	        JsonNode stationsArray = rootNode.get("ev_stations");

	        if (stationsArray != null && stationsArray.isArray()) {
	            for (JsonNode ev : stationsArray) {
	                String franchiseName = ev.get("franchise_name").asText();
	                String siteId = ev.get("registration").asText();
	                String siteName = ev.get("site_name").asText();
	                String stationName = ev.get("station_name").asText();
	                String stationAddress = ev.get("address").asText();
	                String applicationNumber = ev.has("application_number") ? ev.get("application_number").asText().toLowerCase() : "";
	                double lat = ev.get("coordinates").get("latitude").asDouble();
	                double lon = ev.get("coordinates").get("longitude").asDouble();

	                if (nameFilter != null && !nameFilter.isEmpty()) {
	                    String nf = nameFilter.trim().toLowerCase();
	                    if (!franchiseName.toLowerCase().contains(nf) &&
	                        !stationName.toLowerCase().contains(nf)) {
	                        continue;
	                    }
	                }
	                if (latitude != null && longitude != null) {
	                    double distanceKm = calculateDistanceKm(latitude, longitude, lat, lon);
	                    if (distanceKm > effectiveRadius) continue;
	                }
	                if ((from != null && !from.isEmpty()) ||
	                    (to != null && !to.isEmpty())) {

	                    boolean matchesFrom = false;
	                    boolean matchesTo = false;

	                    if (from != null && !from.isEmpty()) {
	                        List<String> fromKeywords = Arrays.asList(from.toLowerCase().split("\\s+"));
	                        matchesFrom = fromKeywords.stream()
	                                .anyMatch(k -> stationAddress.toLowerCase().contains(k));
	                    } else {
	                        matchesFrom = true;
	                    }

	                    if (to != null && !to.isEmpty()) {
	                        List<String> toKeywords = Arrays.asList(to.toLowerCase().split("\\s+"));
	                        matchesTo = toKeywords.stream()
	                                .anyMatch(k -> stationAddress.toLowerCase().contains(k));
	                    } else {
	                        matchesTo = true;
	                    }
	                    if (!(matchesFrom || matchesTo)) continue;
	                }
	                if (search != null && !search.isEmpty()) {
	                    String s = search.toLowerCase();
	                    boolean matchesSearch = franchiseName.toLowerCase().contains(s)
	                            || siteName.toLowerCase().contains(s)
	                            || stationName.toLowerCase().contains(s)
	                            || stationAddress.toLowerCase().contains(s)
	                            || applicationNumber.toLowerCase().contains(s);
	                    if (!matchesSearch) continue;
	                }
	                Map<String, Object> location = new HashMap<>();
	                location.put("address", stationAddress);
	                location.put("latitude", lat);
	                location.put("longitude", lon);
	                List<Map<String, Object>> locationsList = List.of(location);

	                Map<String, Object> port = new HashMap<>();
	                port.put("connectorType", null);
	                port.put("billingAmount", null);
	                port.put("connectorId", null);
	                port.put("capacity", ev.has("capacity") ? ev.get("capacity").asText() : null);
	                port.put("connectorName", null);
	                port.put("billing units", null);
	                port.put("portId", siteId);
	                port.put("powerType", null);

	                Map<String, Object> status = new HashMap<>();
	                status.put("statusId", siteId);
	                status.put("lastContactedTime", null);
	                status.put("status", null);
	                port.put("statusNotifications", List.of(status));

	                Map<String, Object> station = new HashMap<>();
	                station.put("serial number", ev.get("application_number").asText());
	                station.put("current Type", null);
	                station.put("stationName", stationName);
	                station.put("ports", List.of(port));
	                station.put("last heartbeat", System.currentTimeMillis());
	                station.put("stationId", siteId);
	                station.put("status", null);

	                Map<String, Object> site = new HashMap<>();
	                site.put("managerEmail", null);
	                site.put("managerPhone", null);
	                site.put("managerName", franchiseName);
	                site.put("ownerOrgId", null);
	                site.put("ownerId", null);
	                site.put("totalStations", null);
	                site.put("totalPorts", null);
	                site.put("availablePorts", null);
	                site.put("siteId", siteId);
	                site.put("siteName", siteName);
	                site.put("locations", locationsList);
	                site.put("stations", List.of(station));

	                result.add(site);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return result;
	}
    
	private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
	    final int EARTH_RADIUS = 6371; // km
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    return EARTH_RADIUS * c;
	}
	
	@Override
	public Map<String, Object> getPendingRequestsCount() {
	    String query = "SELECT " +
	                   "SUM(CASE WHEN category = 'FRANCHISE' AND status = 0 THEN 1 ELSE 0 END) AS franchisePending, " +
	                   "SUM(CASE WHEN category = 'SITE' AND status = 0 THEN 1 ELSE 0 END) AS sitePending, " +
	                   "SUM(CASE WHEN category = 'STATION' AND status = 0 THEN 1 ELSE 0 END) AS stationPending " +
	                   "FROM requested_franchises";

	    List<Map<String, Object>> result = generalDao.findAliasData(query);

	    return result.isEmpty() ? Map.of("franchisePending", 0, "sitePending", 0, "stationPending", 0)
	                            : result.get(0);
	}
	
	@Transactional
	public void verifyAndProcessEvStations(EvStationDto.EvStation[] evStations) {
	    for (EvStationDto.EvStation evStation : evStations) {
	        try {
	            processSingleEvStation(evStation);
	            updateRequestedFranchiseStatus(evStation);

	        } catch (Exception e) {
	            System.err.println("Failed to process EV station: " + evStation.getSiteName() + " - " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	}
	
	private void updateRequestedFranchiseStatus(EvStationDto.EvStation evStation) throws UserNotFoundException {
	    try {
	        String hql = "FROM RequestedFranchises WHERE " +
	                    "franchiseName = :franchiseName AND " +
	                    "sitename = :siteName AND " +
	                    "stationName = :stationName AND " +
	                    "address = :address";
	        
	        Map<String, Object> params = new HashMap<>();
	        params.put("franchiseName", evStation.getFranchiseName());
	        params.put("siteName", evStation.getSiteName());
	        params.put("stationName", evStation.getStationName());
	        params.put("address", evStation.getAddress());
	        
	        List<RequestedFranchises> requestedFranchises = generalDao.findByHQL(hql, params);
	        
	        if (!requestedFranchises.isEmpty()) {
	            RequestedFranchises franchise = requestedFranchises.get(0);
	            franchise.setStatus(true); // Set status to true when verified
	            
	            generalDao.savOrupdate(franchise);
	            System.out.println("Updated requested franchise status to verified for: " + evStation.getSiteName());
	        } else {
	            System.out.println("No matching requested franchise found for: " + evStation.getSiteName());
	        }
	        
	    } catch (Exception e) {
	        System.err.println("Error updating requested franchise status: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	private void processSingleEvStation(EvStationDto.EvStation evStation) {
	    try {
	        System.out.println("Starting to process: " + evStation.getSiteName());
	        
	        User franchiseUser = createOrGetFranchiseUser(evStation);
	        System.out.println("User created/found with ID: " + franchiseUser.getId());
	        
	        Owner_Orgs franchiseOrg = createOrGetFranchiseOrg(evStation, franchiseUser);
	        System.out.println("Org created/found with ID: " + franchiseOrg.getId());
	        
	        linkUserToOwnerOrg(franchiseUser, franchiseOrg);
	        System.out.println("User linked to org");
	        
	        Site site = createSite(evStation, franchiseOrg, franchiseUser);
	        System.out.println("Site created with ID: " + site.getId());
	        
	        createStationWithPortsAndStatus(evStation, site);
	        System.out.println("Station and ports created");
	        
	        System.out.println("Successfully processed EV station: " + evStation.getSiteName());
	        
	    } catch (Exception e) {
	        System.err.println("Error processing EV station: " + evStation.getSiteName() + " - " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Failed to process station: " + evStation.getSiteName(), e);
	    }
	}

	private void linkUserToOwnerOrg(User franchiseUser, Owner_Orgs franchiseOrg) {
		// TODO Auto-generated method stub
		
	}
	private User createOrGetFranchiseUser(EvStationDto.EvStation evStation) throws UserNotFoundException {
    String hql = "FROM User WHERE orgName = :orgName";
    Map<String, Object> params = new HashMap<>();
    params.put("orgName", evStation.getFranchiseName());

    List<User> existingUsers = generalDao.findByHQL(hql, params);
    if (!existingUsers.isEmpty()) {
        return existingUsers.get(0);
    }
    
    User user = new User();
    user.setFullname(evStation.getFranchiseName());
    user.setUsername(evStation.getFranchiseName()
            .replaceAll("[^a-zA-Z0-9]", "")
            .toLowerCase());

    String email = evStation.getEmail() != null
            ? evStation.getEmail()
            : evStation.getFranchiseName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + "@franchise.com";
    user.setEmail(email);

    String mobileNumber = evStation.getMobileNumber() != null
            ? evStation.getMobileNumber()
            : "90000" + String.valueOf(System.currentTimeMillis()).substring(9, 13);
    user.setMobileNumber(mobileNumber);

    user.setEnabled(true);
    user.setOrgId(WHITELABEL_ORG_ID);
    user.setOrgName(evStation.getFranchiseName());
    user.setUpdatedAt(LocalDateTime.now());

    User savedUser = (User) generalDao.save(user);

    if (savedUser.getId() == null) {
        throw new RuntimeException("❌ Failed to save franchise user - ID is null");
    }

    Password password = new Password();
    password.setPassword("defaultPassword123");
    password.setUser(savedUser);
    password.setCreatedAt(LocalDateTime.now());
    generalDao.save(password);

    Usersinroles userRole = new Usersinroles();
    userRole.setUser(savedUser);
    userRole.setRole_id(FRANCHISE_OWNER_ROLE_ID);
    generalDao.save(userRole);

    System.out.println("✅ Franchise user created: " + savedUser.getUsername() + " (ID: " + savedUser.getId() + ")");
    return savedUser;
}

	private Owner_Orgs createOrGetFranchiseOrg(EvStationDto.EvStation evStation, User franchiseUser) throws UserNotFoundException {
	    String franchiseName = evStation.getFranchiseName();
	    
	    String hql = "FROM Owner_Orgs WHERE orgName = :orgName";
	    Map<String, Object> params = new HashMap<>();
	    params.put("orgName", franchiseName);

	    List<Owner_Orgs> existingOrgs = generalDao.findByHQL(hql, params);
	    
	    if (!existingOrgs.isEmpty()) {
	        Owner_Orgs existingOrg = existingOrgs.get(0);
	        
	        if (!WHITELABEL_ORG_ID.equals(existingOrg.getWhitelabelId())) {
	            System.out.println("🔄 Correcting whitelabelId for " + franchiseName + 
	                             " from " + existingOrg.getWhitelabelId() + " to " + WHITELABEL_ORG_ID);
	            existingOrg.setWhitelabelId(WHITELABEL_ORG_ID);
	            generalDao.update(existingOrg);
	        }	        
	        ensureUserOrgMapping(franchiseUser, existingOrg);
	        return existingOrg;
	    }
	    Owner_Orgs org = new Owner_Orgs();
	    org.setOrgName(franchiseName);
	    org.setWhitelabelId(WHITELABEL_ORG_ID);
	    org.setDriverFranchise(false);

	    Owner_Orgs savedOrg = (Owner_Orgs) generalDao.save(org);
	    ensureUserOrgMapping(franchiseUser, savedOrg);
	    return savedOrg;
	}

		private void ensureUserOrgMapping(User user, Owner_Orgs org) throws UserNotFoundException {
		    String hql = "FROM users_in_owners u WHERE u.owner_org_id = :orgId AND u.user_id = :userId";
		    Map<String, Object> params = new HashMap<>();
		    params.put("orgId", org.getId());
		    params.put("userId", user.getId());

		    List<users_in_owners> existingMappings = generalDao.findByHQL(hql, params);

		    if (existingMappings.isEmpty()) {
		        users_in_owners mapping = new users_in_owners();
		        mapping.setOwner_org_id(org.getId());
		        mapping.setUser_id(user.getId());
		        generalDao.save(mapping);
		        System.out.println("✅ Mapping created for user_id=" + user.getId() + " and org_id=" + org.getId());
		    } else {
		        System.out.println("ℹ️ Mapping already exists for user_id=" + user.getId() + " and org_id=" + org.getId());
		    }
		}
		
	private Site createSite(EvStationDto.EvStation evStation, Owner_Orgs franchiseOrg, User franchiseUser) throws UserNotFoundException {
	    String hql = "FROM Site WHERE siteName = :siteName";
	    Map<String, Object> params = new HashMap<>();
	    params.put("siteName", evStation.getSiteName());
	    
	    List<Site> existingSites = generalDao.findByHQL(hql, params);
	    if (!existingSites.isEmpty()) {
	        return existingSites.get(0);
	    }
	    
	    Site site = new Site();
	    site.setSiteName(evStation.getSiteName());
//	    site.setManagerName(franchiseUser.getFullname());
//	    site.setManagerEmail(franchiseUser.getEmail());
//	    site.setManagerPhone(franchiseUser.getMobileNumber());
	    site.setOwnerOrg(franchiseOrg.getId());
	    site.setOwnerId(franchiseUser.getId());
	    
	    Site savedSite = (Site) generalDao.save(site);

	    SiteLocationDetails location = new SiteLocationDetails();
	    location.setAddress(evStation.getAddress());

	    location.setLatitude(evStation.getCoordinates().getLatitude());
	    location.setLongitude(evStation.getCoordinates().getLongitude());
	   
	    location.setSite(savedSite);
	    generalDao.save(location);

	    SiteOperationalDetails operational = new SiteOperationalDetails();
	    operational.setSiteStatus("ACTIVE");
	    operational.setSite(savedSite);
	    generalDao.save(operational);
	    
	    return savedSite;
	}

	private void createStationWithPortsAndStatus(EvStationDto.EvStation evStation, Site site) throws UserNotFoundException {

		String hql = "FROM Station WHERE stationName = :stationName AND site.id = :siteId";
	    Map<String, Object> params = new HashMap<>();
	    params.put("stationName", evStation.getStationName());
	    params.put("siteId", site.getId());
	    
	    List<Station> existingStations = generalDao.findByHQL(hql, params);
	    if (!existingStations.isEmpty()) {
	        return; 
	    }

	    Station station = new Station();
	    station.setStationName(evStation.getStationName());

	    station.setOCPPId("OCPP_" + evStation.getApplicationNumber());

	    station.setSerialNo(evStation.getSerialNumber());
	    
	    station.setModel("Default Model");
	    station.setManufacturerId(DEFAULT_MANUFACTURER_ID);
	    station.setFirmware_version("1.0");
	    station.setOcppVersion("1.6");
	    station.setCommunication_method("HTTP");
	    station.setStationStatus("ACTIVE");

	    station.setMax_output_power_kW(extractMaxPower(evStation.getCapacity()));
	    
	    station.setVoltage_range(400.0);

	    station.setCurrent_type(evStation.getConnectorType() != null ? evStation.getConnectorType() : "AC/DC");
	    
	    station.setNumber_of_ports(evStation.getNumberOfChargers());
	    station.setV2G_support(false);
	    station.setPlug_and_charger(true);
	    station.setSite(site);
	    
	    Station savedStation = (Station) generalDao.save(station);

	    for (int i = 1; i <= evStation.getNumberOfChargers(); i++) {

	        Port port = createPort(savedStation, i, evStation);

	        createStatusNotification(savedStation, port);
	    }
	}

	private Port createPort(Station station, int portNumber, EvStationDto.EvStation evStation) throws UserNotFoundException {
	    Port port = new Port();
	    port.setConnectorName(evStation.getConnectorType() != null ? evStation.getConnectorType() : "CCS2");
	    port.setConnector_type(evStation.getPortType() != null ? evStation.getPortType() : "DC");
	    port.setMax_power_kW(extractMaxPower(evStation.getCapacity()));
	    port.setStation(station);
	    return (Port) generalDao.save(port);
	}

	private void createStatusNotification(Station station, Port port) throws UserNotFoundException {
	    StatusNotification statusNotification = new StatusNotification();
	    statusNotification.setStationId(station.getId());
	    statusNotification.setStatus("Available"); // Initial status
	    statusNotification.setPort(port);
	    
	    generalDao.save(statusNotification);
	}

	private Double extractMaxPower(String capacity) {
	    if (capacity == null) return 60.0;
	    	    
	    try {
	        String[] parts = capacity.split("-");
	        if (parts.length > 1) {
	            return Double.parseDouble(parts[1].replaceAll("[^0-9.]", ""));
	        }
	        return Double.parseDouble(capacity.replaceAll("[^0-9.]", ""));
	    } catch (Exception e) {
	        return 60.0;
	    }
	}
	
	@Override
    public Port updatePortPrice(long portId, Double billingamount) {
        try {
            Port port = generalDao.findOneById(new Port(), portId);
            if (port == null) {
                throw new UserNotFoundException("Port not found with ID: " + billingamount);
            }
            port.setBillingAmount(billingamount);
            generalDao.update(port);
            return port;

        } catch (UserNotFoundException e) {
            throw new RuntimeException("Error updating billing amount: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while updating billing amount: " + e.getMessage());
        }
    }
	
	@Override
	@Transactional(noRollbackFor = { RuntimeException.class })
	public PortStatusResponse getPortStatusWithReservationAndPricing(Long portId, Date calculationTime) {
		PortStatusResponse response = new PortStatusResponse();

		try {
			Port port = generalDao.findOneById(new Port(), portId);
			if (port == null) {
				throw new RuntimeException("Port not found with id: " + portId);
			}
			response.setPortId(port.getId());
			response.setBillingAmount(port.getBillingAmount());
			response.setMax_power_kW(port.getMax_power_kW());

			Date calculationTimeIST = convertUTCtoIST(calculationTime);
			response.setCurrentTime(calculationTimeIST);
			String currentStatus = getCurrentPortStatus(portId);
			response.setStatus(currentStatus);

			Map<String, Object> reservationData = getActiveReservationData(portId);

			if (reservationData != null && !reservationData.isEmpty()) {
				calculateReservationDurationAndPricing(response, reservationData, calculationTimeIST);
			} else {
				response.setReservationStartTime(null);
				response.setReservationEndTime(null);
				response.setDurationText("No active reservation");
				response.setDurationHours(0L);
				response.setDurationMinutes(0L);
				response.setUnitsConsumed(0.0);
				response.setCalculatedAmount(0.0);
			}

			return response;

		} catch (Exception e) {
			throw new RuntimeException("Error fetching port status: " + e.getMessage(), e);
		}
	}

	private Map<String, Object> getActiveReservationData(Long portId) {
		try {
			String sql = "SELECT TOP 1 * " + "FROM ocpp_reservation " + "WHERE portId = " + portId + " "
					+ "AND status = 'Reserved' " + "ORDER BY startTime ASC";

			System.out.println("Executing SQL: " + sql);

			List<Map<String, Object>> result = generalDao.findAliasData(sql);
			System.out.println("Query result size: " + (result != null ? result.size() : 0));

			if (result != null && !result.isEmpty()) {
				Map<String, Object> reservation = result.get(0);
				Date startTimeIST = convertUTCtoIST((Date) reservation.get("startTime"));
				Date endTimeIST = convertUTCtoIST((Date) reservation.get("endTime"));

				reservation.put("startTime", startTimeIST);
				reservation.put("endTime", endTimeIST);
				return reservation;
			}

			System.out.println("No active reservation found for portId: " + portId);
			return null;

		} catch (Exception e) {
			System.err.println("Error fetching reservation: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}


	
	private void calculateReservationDurationAndPricing(PortStatusResponse response,
	        Map<String, Object> reservationData, Date calculationTimeIST) {

	    try {
	        Date startTime = (Date) reservationData.get("startTime");
	        Date endTime = (Date) reservationData.get("endTime");
	        String status = reservationData.get("status").toString();

	        if (!"Reserved".equalsIgnoreCase(status)) {
	            System.out.println("Reservation not active, status = " + status);
	            return;
	        }

	        response.setReservationStartTime(startTime);
	        response.setReservationEndTime(endTime);
	        Instant calcInstant = calculationTimeIST.toInstant();
	        Instant startInstant = startTime.toInstant();
	        Instant endInstant = endTime.toInstant();

	        Duration timeUntilStart = Duration.between(calcInstant, startInstant);
	        if (timeUntilStart.isNegative()) {
				timeUntilStart = Duration.ZERO;
			}
	        long hours = timeUntilStart.toHours();
	        long minutes = timeUntilStart.toMinutes() % 60;
	        response.setDurationHours(hours);
	        response.setDurationMinutes(minutes);
	        response.setDurationText(hours + " hours " + minutes + " minutes");
	        Map<String, Object> calcBody = new HashMap<>();
	        double durationHoursDecimal = response.getDurationHours() + response.getDurationMinutes() / 60.0;
	        calcBody.put("timeInHours", durationHoursDecimal);

	        Map<String, Object> calcResult = mobileApiController.calculateEnergy(
	                response.getMax_power_kW(),    // portCapacity
	                response.getBillingAmount(),   // pricePerUnit
	                calcBody
	        );

	        response.setUnitsConsumed((Double) calcResult.get("units"));
	        response.setCalculatedAmount((Double) calcResult.get("amount"));

	    } catch (Exception e) {
	        throw new RuntimeException("Error calculating reservation duration: " + e.getMessage(), e);
	    }
	}


	

	private void calculateEnergyAndCost(PortStatusResponse response, double durationHours) {
		Double billingAmount = response.getBillingAmount();
		Double maxPowerKW = response.getMax_power_kW();
		if (billingAmount == null) {
			billingAmount = getDefaultPortPrice(response.getPortId());
			response.setBillingAmount(billingAmount);
		}

		if (maxPowerKW == null) {
			maxPowerKW = getEstimatedPowerConsumption(response.getPortId());
			response.setMax_power_kW(maxPowerKW);
		}
		double unitsConsumed = maxPowerKW * durationHours;
		double calculatedAmount = unitsConsumed * billingAmount;
		response.setUnitsConsumed(Math.round(unitsConsumed * 100.0) / 100.0);
		response.setCalculatedAmount(Math.round(calculatedAmount * 100.0) / 100.0);

		System.out.printf("BILLING CALCULATION: %.2f hours, %.2f kW, ₹%.2f per kWh%n", durationHours, maxPowerKW,
				billingAmount);
		System.out.printf("RESULT: %.2f kWh, ₹%.2f total%n", response.getUnitsConsumed(),
				response.getCalculatedAmount());
	}

	private Double getDefaultPortPrice(Long portId) {
		try {
			Port port = generalDao.findOneById(new Port(), portId);
			if (port != null && port.getBillingAmount() != null) {
				return port.getBillingAmount();
			}
		} catch (Exception e) {
			System.err.println("Error getting port price: " + e.getMessage());
		}
		return 10.0;
	}

	private void formatTimeFields(PortStatusResponse response) {
		try {
			SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (response.getCurrentTime() != null) {
				response.setCurrentTime(isoFormat.parse(isoFormat.format(response.getCurrentTime())));
			}

			if (response.getReservationStartTime() != null) {
				response.setReservationStartTime(isoFormat.parse(isoFormat.format(response.getReservationStartTime())));
			}

			if (response.getReservationEndTime() != null) {
				response.setReservationEndTime(isoFormat.parse(isoFormat.format(response.getReservationEndTime())));
			}

		} catch (Exception e) {
			System.err.println("Error formatting time fields: " + e.getMessage());
		}
	}

	public static Date getUtcDateFormate(Date date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String utctime = dateFormat.format(date);
			Date parse = dateFormat.parse(utctime);
			System.out.println("UTC time: " + parse);
			return parse;
		} catch (Exception e) {
			e.printStackTrace();
			return new Date();
		}
	}
	private Date convertUTCtoIST(Date utcDate) {
	    Instant instant = utcDate.toInstant();
	    ZonedDateTime istTime = instant.atZone(ZoneId.of("Asia/Kolkata"));
	    return Date.from(istTime.toInstant());
	}
	private String convertUTCToISTString(Date utcDate) {
	    if (utcDate == null) {
			return null;
		}

	    Instant instant = utcDate.toInstant();
	    ZonedDateTime istTime = instant.atZone(ZoneId.of("Asia/Kolkata"));

	    return istTime.toLocalDateTime().toString(); 
	}


	@Override
	public String getCurrentPortStatus(Long portId) {
		try {
			String hql = "FROM StatusNotification sn WHERE sn.port.id = " + portId
					+ " ORDER BY sn.lastContactedTime DESC";
			List<StatusNotification> result = generalDao.findAllHQLQry(new StatusNotification(), hql);
			return result.isEmpty() ? "Unknown" : result.get(0).getStatus();
		} catch (Exception e) {
			System.err.println("Error fetching port status: " + e.getMessage());
			return "Unknown";
		}
	}

	/** Estimate average power consumption based on port capacity */
	private double getEstimatedPowerConsumption(Long portId) {
		try {
			Port port = generalDao.findOneById(new Port(), portId);
			if (port != null && port.getMax_power_kW() != null) {
				return port.getMax_power_kW(); // Use full capacity for estimation
			}
		} catch (Exception e) {
			System.err.println("Error getting power consumption: " + e.getMessage());
		}
		return 0.0; 
	}

	@Override
	public byte[] downloadLogFile(String stationId, String filename) throws Exception {

        String host = "13.232.8.31";   // OCPP AWS server
        String user = "ubuntu";
        String keyPath = "/opt/keys/ocpp16.pem";
        String remotePath = "/home/ubuntu/logs/stationLogs/" + stationId + "/" + filename;

        JSch jsch = new JSch();
        jsch.addIdentity(keyPath);

        Session session = jsch.getSession(user, host, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("cat " + remotePath);
        channel.setInputStream(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        channel.setOutputStream(outputStream);

        channel.connect();

        while (!channel.isClosed()) {
            Thread.sleep(100);
        }

        channel.disconnect();
        session.disconnect();

        return outputStream.toByteArray();
    }
	

}
