package com.ewe.pojo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "site_operations")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "site" })
public class SiteOperationalDetails extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	
	private String openingTime;
	private String closeTime;
	private String siteStatus;
	private String timezone;
	private Site site;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Site.class)
	@JoinColumn(name = "site_id")
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public String getOpeningTime() {
		return openingTime;
	}
	public void setOpeningTime(String openingTime) {
		this.openingTime = openingTime;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	
	public void setSiteStatus(String siteStatus) {
		this.siteStatus = siteStatus;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	 public String getSiteStatus() {
	        return siteStatus;
	    }
	

}
