package com.ewe.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.FavSiteDTO;
import com.ewe.form.SiteDetailsForm;
import com.ewe.pojo.Site;
import com.ewe.pojo.SiteOperationalDetails;
import com.ewe.pojo.Station;

public interface SiteService {

	void addSite(SiteDetailsForm siteForm) throws UserNotFoundException;

	SiteDetailsForm getSiteById(Long id) throws UserNotFoundException;

	void editSite(Long id, SiteDetailsForm siteForm) throws UserNotFoundException;

	List<Map<String, Object>> getAllSitesPaginated(Long orgId);

	List<Site> getAllSites(Long orgId) ;

	void deleteSiteById(Long id) throws UserNotFoundException;
	
	List<Site> getSiteByOrgId(Long id) throws UserNotFoundException;

	Object getAllByTable(String tableName, Long OrgId);

	List<Station> getStationBysiteId(Long id) throws UserNotFoundException;

	void updateSiteOperationalDetails(SiteOperationalDetails updatedDetails) throws UserNotFoundException;

	SiteOperationalDetails findSiteId(Long id);

	List<Map<String, Object>> getAllSitesPaginated(Long orgId, String searh);

	void addFavSiteForUser(Long userId, Long siteId) throws UserNotFoundException;

	List<FavSiteDTO> getFavStationsByUserId(Long userId) throws UserNotFoundException;

	void deleteFavSitesByUserId(Long userId) throws UserNotFoundException;

}
