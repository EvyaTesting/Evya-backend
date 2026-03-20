package com.ewe.form;

import java.util.List;

public class RequestedFranchisesDTO {

    private String category;
    private String franchiseName;
    private String address;
    private String latitude;
    private String longitude;
    private String mobileNumber;
    private String email;
    private Long ownerOrgId;
    private Long siteId;
    private Long userId;

    private List<SiteInfo> sites;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFranchiseName() { return franchiseName; }
    public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getOwnerOrgId() { return ownerOrgId; }
    public void setOwnerOrgId(Long ownerOrgId) { this.ownerOrgId = ownerOrgId; }
    
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public List<SiteInfo> getSites() { return sites; }
    public void setSites(List<SiteInfo> sites) { this.sites = sites; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // Nested class for Site
    public static class SiteInfo {
        private String sitename;
        private String address;
        private String latitude;
        private String longitude;
        private List<StationInfo> stations;

        public String getSitename() { return sitename; }
        public void setSitename(String sitename) { this.sitename = sitename; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getLatitude() { return latitude; }
        public void setLatitude(String latitude) { this.latitude = latitude; }

        public String getLongitude() { return longitude; }
        public void setLongitude(String longitude) { this.longitude = longitude; }

        public List<StationInfo> getStations() { return stations; }
        public void setStations(List<StationInfo> stations) { this.stations = stations; }
    }

    // Nested class for Station
    public static class StationInfo {
        private String stationName;
        private Double chargerCapacity;

        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }

        public Double getChargerCapacity() { return chargerCapacity; }
        public void setChargerCapacity(Double chargerCapacity) { this.chargerCapacity = chargerCapacity; }
    }
}
