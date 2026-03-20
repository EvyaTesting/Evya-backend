package com.ewe.serviceImpl;



import java.util.ArrayList;

import java.util.Arrays;

import java.util.Collections;

import java.util.List;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDate;

import java.time.LocalTime;

import java.time.ZoneId;

import java.time.ZonedDateTime;



import com.ewe.dao.GeneralDao;

import com.ewe.exception.UserNotFoundException;

import com.ewe.form.FavSiteDTO;

import com.ewe.form.SiteDetailsForm;

import com.ewe.pojo.Fav_Sites;

import com.ewe.pojo.Site;

import com.ewe.pojo.SiteFacilities;

import com.ewe.pojo.SiteLocationDetails;

import com.ewe.pojo.SiteOperationalDetails;

import com.ewe.pojo.Station;

import com.ewe.pojo.User;

import com.ewe.service.SiteService;



@Service

@Transactional

public class SiteServiceImpl implements SiteService{


@Autowired

private GeneralDao<?, ?> generalDao;



@Override

public void addSite(SiteDetailsForm siteDTO) throws UserNotFoundException {



// Create Site entity with basic information

Site site = new Site();

site.setSiteName(siteDTO.getSiteName());

site.setManagerName(siteDTO.getManagerName());

site.setManagerEmail(siteDTO.getManagerEmail());

site.setManagerPhone(siteDTO.getManagerPhone());

site.setOwnerOrg(siteDTO.getOwnerOrgId());

site.setOwnerId(siteDTO.getOwnerId());

generalDao.save(site);



// Create and associate SiteLocationDetails

SiteLocationDetails locationDetails = new SiteLocationDetails();

locationDetails.setAddress(siteDTO.getAddress());

locationDetails.setLatitude(siteDTO.getLatitude());

locationDetails.setLongitude(siteDTO.getLongitude());

locationDetails.setSite(site);

generalDao.save(locationDetails);


// Create and associate SiteFacilities

SiteFacilities facilities = new SiteFacilities();

facilities.setParking(siteDTO.getParking());

facilities.setWifi(siteDTO.getWifi());

facilities.setFood(siteDTO.getFood());

facilities.setRestrooms(siteDTO.getRestrooms());

facilities.setSite(site);

generalDao.save(facilities);



// Create and associate SiteOperationalDetails

SiteOperationalDetails operationalDetails = new SiteOperationalDetails();

operationalDetails.setOpeningTime(siteDTO.getOpeningTime());

operationalDetails.setCloseTime(siteDTO.getCloseTime());

operationalDetails.setSiteStatus(siteDTO.getSiteStatus());

operationalDetails.setTimezone(siteDTO.getTimezone());

operationalDetails.setSite(site);

generalDao.save(operationalDetails);

}



public SiteDetailsForm getSiteById(Long id) throws UserNotFoundException {

String query = "SELECT " +

"s.id AS siteId, " +

"s.siteName AS siteName, " +

"s.managerName AS managerName, " +

"s.managerEmail AS managerEmail, " +

"s.managerPhone AS managerPhone, " +

"s.ownerOrg AS ownerOrgId, " + // Changed from ownerOrgId to ownerOrg

"s.ownerId AS ownerId, " +

"sl.address AS address, " +

"sl.latitude AS latitude, " +

"sl.longitude AS longitude, " +

"sf.food AS food, " +

"sf.parking AS parking, " +

"sf.restrooms AS restrooms, " +

"sf.wifi AS wifi, " +

"so.openingTime AS openingTime, " +

"so.closeTime AS closeTime, " +

"so.siteStatus AS siteStatus, " +

"so.timezone AS timezone " +

"FROM site s " +

"LEFT JOIN site_location sl ON s.id = sl.site_id " +

"LEFT JOIN site_facilities sf ON s.id = sf.site_id " +

"LEFT JOIN site_operations so ON s.id = so.site_id " +

"WHERE s.id = " + id;



Map<String, Object> result = generalDao.getSingleMapData(query);


if (result == null || result.isEmpty()) {

throw new UserNotFoundException("Site not found for id: " + id);

}



SiteDetailsForm form = new SiteDetailsForm();


form.setSiteId(((Number) result.get("siteId")).longValue());

form.setSiteName((String) result.get("siteName"));


// Nullable fields with safe casting

form.setManagerName(result.containsKey("managerName") ? (String) result.get("managerName") : null);

form.setManagerEmail(result.containsKey("managerEmail") ? (String) result.get("managerEmail") : null);

form.setManagerPhone(result.containsKey("managerPhone") ? (String) result.get("managerPhone") : null);


form.setOwnerOrgId(result.get("ownerOrgId") != null ? ((Number) result.get("ownerOrgId")).longValue() : 0L);

form.setOwnerId(result.get("ownerId") != null ? ((Number) result.get("ownerId")).longValue() : 0L);


form.setAddress(result.containsKey("address") ? (String) result.get("address") : null);

form.setLatitude(result.containsKey("latitude") ? (String) result.get("latitude") : null);

form.setLongitude(result.containsKey("longitude") ? (String) result.get("longitude") : null);


form.setParking(result.containsKey("parking") && Boolean.TRUE.equals(result.get("parking")));

form.setWifi(result.containsKey("wifi") && Boolean.TRUE.equals(result.get("wifi")));

form.setFood(result.containsKey("food") && Boolean.TRUE.equals(result.get("food")));

form.setRestrooms(result.containsKey("restrooms") && Boolean.TRUE.equals(result.get("restrooms")));


// form.setOpeningTime(result.containsKey("openingTime") ? (String) result.get("openingTime") : null);

// form.setCloseTime(result.containsKey("closeTime") ? (String) result.get("closeTime") : null);

form.setSiteStatus(result.containsKey("siteStatus") ? (String) result.get("siteStatus") : null);

// form.setTimezone(result.containsKey("timezone") ? (String) result.get("timezone") : null);







String openingTime = (String) result.get("openingTime");

String closeTime = (String) result.get("closeTime");

String timezone = (String) result.get("timezone");

System.out.println("DB Opening Time: " + openingTime);

System.out.println("Selected Timezone: " + timezone);



String convertedTime = convertTime(openingTime, timezone);



System.out.println("Converted Time: " + convertedTime);

form.setOpeningTime(convertTime(openingTime, timezone));

form.setCloseTime(convertTime(closeTime, timezone));

form.setTimezone(timezone);

// Fetch multiple location records (if applicable)

String locationQuery = "SELECT address, latitude, longitude FROM site_location WHERE site_id = " + id;

form.setLocation(generalDao.getMapData(locationQuery));



return form;

}



// @Override

// public void editSite(Long id, SiteDetailsForm siteDTO) throws UserNotFoundException {

// Site siteTemplate = new Site();

// Site site = generalDao.findOneById(siteTemplate, id);

//

// if (site == null) {

// throw new UserNotFoundException("Site not found for id: " + id);

// }

//

// // Update basic site info

// site.setSiteName(siteDTO.getSiteName());

// site.setManagerName(siteDTO.getManagerName());

// site.setManagerEmail(siteDTO.getManagerEmail());

// site.setManagerPhone(siteDTO.getManagerPhone());

// site.setOwnerOrg(siteDTO.getOwnerOrgId());

// site.setOwnerId(siteDTO.getOwnerId());

// generalDao.savOrupdate(site);

//

// // Update SiteLocationDetails

// SiteLocationDetails locationDetails = site.getLocation().isEmpty() ? new SiteLocationDetails() : site.getLocation().iterator().next();

// locationDetails.setAddress(siteDTO.getAddress());

// locationDetails.setLatitude(siteDTO.getLatitude());

// locationDetails.setLongitude(siteDTO.getLongitude());

// locationDetails.setSite(site);

// generalDao.savOrupdate(locationDetails);

//

// // Update SiteFacilities

// SiteFacilities facilities = site.getFacilities().isEmpty() ? new SiteFacilities() : site.getFacilities().iterator().next();

// facilities.setParking(siteDTO.isParking());

// facilities.setWifi(siteDTO.isWifi());

// facilities.setFood(siteDTO.isFood());

// facilities.setRestrooms(siteDTO.isRestrooms());

// facilities.setSite(site);

// generalDao.savOrupdate(facilities);

//

// // Update SiteOperationalDetails

// SiteOperationalDetails operationalDetails = site.getOperations().isEmpty() ? new SiteOperationalDetails() : site.getOperations().iterator().next();

// operationalDetails.setOpeningTime(siteDTO.getOpeningTime());

// operationalDetails.setCloseTime(siteDTO.getCloseTime());

// operationalDetails.setSiteStatus(siteDTO.getSiteStatus());

// operationalDetails.setTimezone(siteDTO.getTimezone());

// operationalDetails.setSite(site);

// generalDao.savOrupdate(operationalDetails);

// }


@Override

public void editSite(Long id, SiteDetailsForm siteDTO) throws UserNotFoundException {

Site site = generalDao.findOneById(new Site(), id);



if (site == null) {

throw new UserNotFoundException("Site not found for id: " + id);

}



if (siteDTO.getSiteName() != null) site.setSiteName(siteDTO.getSiteName());

if (siteDTO.getManagerName() != null) site.setManagerName(siteDTO.getManagerName());

if (siteDTO.getManagerEmail() != null) site.setManagerEmail(siteDTO.getManagerEmail());

if (siteDTO.getManagerPhone() != null) site.setManagerPhone(siteDTO.getManagerPhone());

// if (siteDTO.getOwnerOrgId() != null) site.setOwnerOrg(siteDTO.getOwnerOrgId());

// if (siteDTO.getOwnerId() != null) site.setOwnerId(siteDTO.getOwnerId());



generalDao.savOrupdate(site);



SiteLocationDetails locationDetails = site.getLocation().isEmpty()

? new SiteLocationDetails()

: site.getLocation().iterator().next();



if (siteDTO.getAddress() != null) locationDetails.setAddress(siteDTO.getAddress());

if (siteDTO.getLatitude() != null) locationDetails.setLatitude(siteDTO.getLatitude());

if (siteDTO.getLongitude() != null) locationDetails.setLongitude(siteDTO.getLongitude());



locationDetails.setSite(site);

generalDao.savOrupdate(locationDetails);



// SiteFacilities facilities = site.getFacilities().isEmpty()

// ? new SiteFacilities()

// : site.getFacilities().iterator().next();

//

// facilities.setSite(site);

// generalDao.savOrupdate(facilities);


SiteFacilities facilities = site.getFacilities().isEmpty()

? new SiteFacilities()

: site.getFacilities().iterator().next();



if (siteDTO.getParking() != null) {

facilities.setParking(siteDTO.getParking());

}



if (siteDTO.getWifi() != null) {

facilities.setWifi(siteDTO.getWifi());

}



if (siteDTO.getFood() != null) {

facilities.setFood(siteDTO.getFood());

}



if (siteDTO.getRestrooms() != null) {

facilities.setRestrooms(siteDTO.getRestrooms());

}



facilities.setSite(site);

generalDao.savOrupdate(facilities);





SiteOperationalDetails operationalDetails = site.getOperations().isEmpty()

? new SiteOperationalDetails()

: site.getOperations().iterator().next();



if (siteDTO.getOpeningTime() != null) operationalDetails.setOpeningTime(siteDTO.getOpeningTime());

if (siteDTO.getCloseTime() != null) operationalDetails.setCloseTime(siteDTO.getCloseTime());

if (siteDTO.getTimezone() != null) operationalDetails.setTimezone(siteDTO.getTimezone());

if (siteDTO.getSiteStatus() != null) operationalDetails.setSiteStatus(siteDTO.getSiteStatus());



operationalDetails.setSite(site);

generalDao.savOrupdate(operationalDetails);

}



@Override

public List<Map<String, Object>> getAllSitesPaginated(Long orgId) {

return getAllSitesPaginated(orgId, null); // Maintain backward compatibility

}



@Override

public List<Map<String, Object>> getAllSitesPaginated(Long orgId, String search) {

String query =

"SELECT s.siteName AS sitename, s.id AS siteId, o.orgName AS owner_orgName, " +

"wl.orgName AS white_lable_orgName, o.id AS ownerOrgId, o.whitelabelId " +

"FROM site s " +

"LEFT JOIN owner_orgs o ON o.id = s.ownerOrg " +

"LEFT JOIN white_lable_orgs wl ON wl.id = o.whitelabelId ";



// Build WHERE conditions

List<String> conditions = new ArrayList<>();


// Organization filter

if (orgId != null && orgId != 1) {

conditions.add("(o.whitelabelId = " + orgId + " OR s.ownerOrg = " + orgId + ")");

}


// Search filter

if (search != null && !search.trim().isEmpty()) {

String searchTerm = "%" + search.toLowerCase() + "%";

conditions.add("(LOWER(s.siteName) LIKE '" + searchTerm + "' " +

"OR LOWER(o.orgName) LIKE '" + searchTerm + "' " +

"OR LOWER(wl.orgName) LIKE '" + searchTerm + "')");

}


// Combine conditions

if (!conditions.isEmpty()) {

query += " WHERE " + String.join(" AND ", conditions);

}



return generalDao.getMapData(query);

}



@Override

public List<Site> getAllSites(Long orgId) {

if (orgId == null || orgId == 1) {


return generalDao.findAll(new Site());

} else {

try {


String whitelabelQuery = "SELECT s.* FROM site s " +

"JOIN owner_orgs o ON s.ownerOrg = o.id " +

"WHERE o.whitelabelId = " + orgId;

List<Site> whitelabelSites = generalDao.findAllSQLQuery(new Site(), whitelabelQuery);

if (whitelabelSites == null || whitelabelSites.isEmpty()) {


String franchiseQuery = "SELECT s.* FROM site s " +

"WHERE s.ownerOrg = " + orgId;

return generalDao.findAllSQLQuery(new Site(), franchiseQuery);

}


return whitelabelSites;

} catch (UserNotFoundException e) {

e.printStackTrace();

return Collections.emptyList(); // Better to return empty list than null

}

}

}

@Override

@Transactional

public void deleteSiteById(Long id) throws UserNotFoundException {

try {

Site siteTemplate = new Site();

Site site = generalDao.findOneById(siteTemplate, id);

if (site == null) {

throw new UserNotFoundException("Site not found with id: " + id);

}



List<Station> stations = getStationBysiteId(id);



boolean hasActiveOrMaintenance = stations.stream()

.anyMatch(station -> {

String status = station.getStationStatus();

return !"Inactive".equalsIgnoreCase(status);

});



if (hasActiveOrMaintenance) {

throw new IllegalStateException("Cannot delete site. One or more stations are in 'Active' or 'Maintenance' status.");

}



for (Station station : stations) {

station.setSite(null);

generalDao.update(station);

}



generalDao.delete(site);



} catch (Exception e) {

throw new UserNotFoundException("Failed to delete site: " + e.getMessage(), e);

}

}



@Override

public List<Site> getSiteByOrgId(Long id) throws UserNotFoundException {

String query = "SELECT * FROM site WHERE ownerOrg = " + id; // Prefer parameterized or JPA

List<Site> sites = generalDao.findAllOrg(new Site(), query);



if (sites == null || sites.isEmpty()) {

// throw new UserNotFoundException("No sites found for organization ID: " + id);

return null;

}



return sites;

}



@Override

public Object getAllByTable(String tableName, Long OrgId) {

// Security: validate against known tables

List<String> allowedTables = Arrays.asList("site", "station");

String lowerTableName = tableName.toLowerCase();


if (!allowedTables.contains(lowerTableName)) {

throw new IllegalArgumentException("Invalid table name");

}



String columnName = tableName + "Name";

String query;



if (OrgId == null || OrgId == 1) { // id=1 means get all data

query = "SELECT DISTINCT id, " + columnName + " FROM " + tableName;

} else {

if ("site".equalsIgnoreCase(tableName)) {

query = "SELECT DISTINCT s.id, s.siteName FROM site s " +

"LEFT JOIN owner_orgs o ON s.ownerOrg = o.id " +

"WHERE (o.whitelabelId = " + OrgId + " OR s.ownerOrg = " + OrgId + ")";

} else {

query = "SELECT DISTINCT st.id, st.stationName FROM station st " +

"JOIN site s ON st.site_id = s.id " +

"LEFT JOIN owner_orgs o ON s.ownerOrg = o.id " +

"WHERE (o.whitelabelId = " + OrgId + " OR s.ownerOrg = " + OrgId + ")";

}

}



return generalDao.getMapData(query);

}


@Override

public List<Station> getStationBysiteId(Long id) throws UserNotFoundException {

String query = "Select * from station where site_id = " + id;

List<Station> net = generalDao.findAllSQLQuery(new Station(), query);

System.out.println("netw :" + net);

return net;

}


@Override

public void updateSiteOperationalDetails(SiteOperationalDetails input) throws UserNotFoundException {

SiteOperationalDetails existing = findSiteId(input.getId());

if (existing == null) throw new RuntimeException("Not found");



// Only update non-null fields from input

if (input.getOpeningTime() != null) {

existing.setOpeningTime(input.getOpeningTime());

}



if (input.getCloseTime() != null) {

existing.setCloseTime(input.getCloseTime());

}



if (input.getSiteStatus() != null) {

existing.setSiteStatus(input.getSiteStatus());

}



if (input.getTimezone() != null && !input.getTimezone().isEmpty()) {

existing.setTimezone(input.getTimezone());

}



generalDao.savOrupdate(existing);

}



@Override

public SiteOperationalDetails findSiteId(Long id) {

try {

// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);

String query = "Select * from site_operations where site_id = " + id;

SiteOperationalDetails net = generalDao.findOneSQLQuery(new SiteOperationalDetails(), query);

System.out.println("netw :" + net);

return net;

} catch (Exception e) {

e.printStackTrace();

}

return null;

}


@Override

public void addFavSiteForUser(Long userId, Long siteId) throws UserNotFoundException {



if (siteId == null) {

throw new IllegalArgumentException("SiteId is required");

}

User user = generalDao.findOneSQLQuery(new User(), "SELECT * FROM Users WHERE id = " + userId);

if (user == null) {

throw new UserNotFoundException("User not found with id: " + userId);

}

Site site = generalDao.findOneSQLQuery(new Site(), "SELECT * FROM site WHERE id = " + siteId);

if (site == null) {

throw new UserNotFoundException("Site not found with id: " + siteId);

}

String checkQuery = "SELECT * FROM fav_sites WHERE user_id = " + userId +

" AND site_id = " + siteId;

Fav_Sites existing = generalDao.findOneSQLQuery(new Fav_Sites(), checkQuery);

if (existing != null) {

return;

}

Fav_Sites fav = new Fav_Sites();

fav.setUser(user);

fav.setSite(site);

generalDao.save(fav);

}


@Override

public List<FavSiteDTO> getFavStationsByUserId(Long userId) throws UserNotFoundException {



String favQuery = "SELECT * FROM fav_sites WHERE user_id = " + userId;

List<Fav_Sites> favList = generalDao.findAllSQLQuery(new Fav_Sites(), favQuery);



if (favList == null || favList.isEmpty()) {

return Collections.emptyList(); // no favourites found

}



List<FavSiteDTO> favDtos = new ArrayList<>();

for (Fav_Sites fav : favList) {

FavSiteDTO dto = new FavSiteDTO();

if (fav.getSite() != null) {

String stQuery = "SELECT * FROM site WHERE id = " + fav.getSite().getId();

Site site = generalDao.findOneSQLQuery(new Site(), stQuery);

if (site != null) {

dto.setsiteId(site.getId());

dto.setSiteName(site.getSiteName());

}

}

favDtos.add(dto);

}

return favDtos;

}


@Override

@Transactional

public void deleteFavSitesByUserId(Long userId) throws UserNotFoundException {



String checkSql = "SELECT * FROM fav_sites WHERE user_id = " + userId;

List<Fav_Sites> favList = generalDao.findAllSQLQuery(new Fav_Sites(), checkSql);

if (favList == null || favList.isEmpty()) {

throw new UserNotFoundException("No favourite sites found for userId: " + userId);

}

String deleteSql = "DELETE FROM fav_sites WHERE user_id = " + userId;

String result = generalDao.deleteSqlQuiries(deleteSql);

if (!"success".equalsIgnoreCase(result)) {

throw new UserNotFoundException("Failed to delete favourite sites");

}



}

public String convertTime(String time, String timezone) {



if (time == null || timezone == null) {

return time;

}



try {



LocalTime localTime = LocalTime.parse(time);



// Assume DB time is stored in UTC

ZonedDateTime utcTime = localTime.atDate(LocalDate.now())

.atZone(ZoneId.of("UTC"));



ZoneId targetZone;



switch (timezone) {

case "IST":

targetZone = ZoneId.of("Asia/Kolkata");

break;

case "EST":

targetZone = ZoneId.of("America/New_York");

break;

case "PST":

targetZone = ZoneId.of("America/Los_Angeles");

break;

case "UTC":

targetZone = ZoneId.of("UTC");

break;

default:

targetZone = ZoneId.of(timezone);

}



ZonedDateTime converted = utcTime.withZoneSameInstant(targetZone);



return converted.toLocalTime().toString();



} catch (Exception e) {

e.printStackTrace();

return time;

}

}

}