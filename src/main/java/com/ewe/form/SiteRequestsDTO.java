package com.ewe.form;

import java.util.List;

public class SiteRequestsDTO {
    private String sitename;
    private String address;
    private String latitude;
    private String longitude;
    private List<StationRequestsDTO> stations;

    // Getters and Setters
    public String getSitename() {
        return sitename;
    }
    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<StationRequestsDTO> getStations() {
        return stations;
    }
    public void setStations(List<StationRequestsDTO> stations) {
        this.stations = stations;
    }
}
