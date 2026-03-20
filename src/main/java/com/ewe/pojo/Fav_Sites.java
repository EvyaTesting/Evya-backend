package com.ewe.pojo;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name = "fav_sites")
public class Fav_Sites extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	private User user;
	private Site site;
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	public User getUser() {
		return user;		
	}
	public void setUser (User user) {
		this.user = user;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Site.class)
	@JoinColumn(name = "site_id")
	@JsonIgnore
	public Site getSite() {
		return site;
	}
	public void setSite (Site siteId) {
		this.site = siteId;
	}
}
