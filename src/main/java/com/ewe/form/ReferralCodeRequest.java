package com.ewe.form;

import java.time.LocalDateTime;

import java.util.Date;
import java.util.List;


import java.util.Date;
import java.util.List;

public class ReferralCodeRequest {
	 private Double offerPercentage;
	    private Date validFrom;
	    private Date validTo;
	    private Boolean allSites;
	    private List<Long> siteIds;
		public Double getOfferPercentage() {
			return offerPercentage;
		}
		public void setOfferPercentage(Double offerPercentage) {
			this.offerPercentage = offerPercentage;
		}
		public Date getValidFrom() {
			return validFrom;
		}
		public void setValidFrom(Date validFrom) {
			this.validFrom = validFrom;
		}
		public Date getValidTo() {
			return validTo;
		}
		public void setValidTo(Date validTo) {
			this.validTo = validTo;
		}
		public Boolean getAllSites() {
			return allSites;
		}
		public void setAllSites(Boolean allSites) {
			this.allSites = allSites;
		}
		public List<Long> getSiteIds() {
			return siteIds;
		}
		public void setSiteIds(List<Long> siteIds) {
			this.siteIds = siteIds;
		}
		public boolean isAllSites() {
	        return Boolean.TRUE.equals(allSites);
	    }
}