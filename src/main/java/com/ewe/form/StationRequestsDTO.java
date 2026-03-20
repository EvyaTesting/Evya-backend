package com.ewe.form;

public class StationRequestsDTO {
    private String stationName;
    private Double chargerCapacity;
    private String address;
    private Double latitude;
    private Double longitude;

    // Getters and Setters
    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Double getChargerCapacity() {
        return chargerCapacity;
    }
    public void setChargerCapacity(Double chargerCapacity) {
        this.chargerCapacity = chargerCapacity;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
