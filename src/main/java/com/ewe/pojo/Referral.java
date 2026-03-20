package com.ewe.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import javax.persistence.Entity;
@Entity
@Table(name = "referral_code")
public class Referral extends BaseEntity {

    private String referralCode;
    private Double offerPercentage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date validTo;

    private boolean allSites;

    private Set<Site> sites;

    @Column(name = "referral_code")
    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    @Column(name = "offer_percentage")
    public Double getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Double offerPercentage) {
        this.offerPercentage = offerPercentage;
    }

    @Column(name = "valid_from")
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Column(name = "valid_to")
    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    @Column(name = "all_sites")
    public boolean isAllSites() {
        return allSites;
    }

    public void setAllSites(boolean allSites) {
        this.allSites = allSites;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "referral_code_sites",
        joinColumns = @JoinColumn(name = "referral_code_id"),
        inverseJoinColumns = @JoinColumn(name = "site_id")
    )
    public Set<Site> getSites() {
        return sites;
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
    }
}
