package com.ewe.pojo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "site_facilities")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "site" })
public class SiteFacilities extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private boolean parking;
	private boolean wifi;
	private boolean food;
	private boolean restrooms;
	private Site site;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Site.class)
	@JoinColumn(name = "site_id")	
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public boolean isParking() {
		return parking;
	}
	public void setParking(boolean parking) {
		this.parking = parking;
	}
	public boolean isWifi() {
		return wifi;
	}
	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}
	public boolean isFood() {
		return food;
	}
	public void setFood(boolean food) {
		this.food = food;
	}
	public boolean isRestrooms() {
		return restrooms;
	}
	public void setRestrooms(boolean restrooms) {
		this.restrooms = restrooms;
	}
}
