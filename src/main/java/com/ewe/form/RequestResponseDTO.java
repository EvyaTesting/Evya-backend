package com.ewe.form;

import java.util.List;

public class RequestResponseDTO {
    private Long id;
    private String category;
    private String franchiseName;
    private String address;
    private String latitude;
    private String longitude;
    private String mobileNumber;
    private String email;
    private Boolean status;

    private List<SiteInfo> sites; // Nested sites with stations

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public List<SiteInfo> getSites() { return sites; }
    public void setSites(List<SiteInfo> sites) { this.sites = sites; }

    // Nested DTO for Site
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

    // Nested DTO for Station
    public static class StationInfo {
        private String stationName;
        private Double chargerCapacity;

        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }
        public Double getChargerCapacity() { return chargerCapacity; }
        public void setChargerCapacity(Double chargerCapacity) { this.chargerCapacity = chargerCapacity; }
    }
}
