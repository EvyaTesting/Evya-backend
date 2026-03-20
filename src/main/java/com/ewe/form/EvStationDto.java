package com.ewe.form;


import com.fasterxml.jackson.annotation.JsonProperty;

public class EvStationDto {
    @JsonProperty("ev_stations")
    private EvStation[] evStations;

    // Getters and setters
    public EvStation[] getEvStations() {
        return evStations;
    }

    public void setEvStations(EvStation[] evStations) {
        this.evStations = evStations;
    }

    public static class EvStation {
        @JsonProperty("site_name")
        private String siteName;
        
        @JsonProperty("station_name")
        private String stationName;
        
        @JsonProperty("franchise_name")
        private String franchiseName;
        
        @JsonProperty("address")
        private String address;
        
        @JsonProperty("coordinates")
        private Coordinates coordinates;
        
        @JsonProperty("capacity")
        private String capacity;
        
        @JsonProperty("number_of_chargers")
        private Integer numberOfChargers;
        
        @JsonProperty("application_number")
        private String applicationNumber;
        
        @JsonProperty("district")
        private String district;
        
        @JsonProperty("registration_date")
        private String registrationDate;
        
        @JsonProperty("registration")
        private String registration;
        
        @JsonProperty("icon")
        private String icon;
        @JsonProperty("connectorType")
        private String connectorType;
        @JsonProperty("portType")
        private String portType;
        @JsonProperty("email")
        private String email;
        @JsonProperty("mobileNumber")
        private String mobileNumber;
        @JsonProperty("serialNumber")
        private String serialNumber;
        // Getters and setters
        public String getSiteName() { return siteName; }
        public void setSiteName(String siteName) { this.siteName = siteName; }
        
        public String getStationName() { return stationName; }
        public void setStationName(String stationName) { this.stationName = stationName; }
        
        public String getFranchiseName() { return franchiseName; }
        public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public Coordinates getCoordinates() { return coordinates; }
        public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
        
        public String getCapacity() { return capacity; }
        public void setCapacity(String capacity) { this.capacity = capacity; }
        
        public Integer getNumberOfChargers() { return numberOfChargers; }
        public void setNumberOfChargers(Integer numberOfChargers) { this.numberOfChargers = numberOfChargers; }
        
        public String getApplicationNumber() { return applicationNumber; }
        public void setApplicationNumber(String applicationNumber) { this.applicationNumber = applicationNumber; }
        
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        
        public String getRegistrationDate() { return registrationDate; }
        public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
        
        public String getRegistration() { return registration; }
        public void setRegistration(String registration) { this.registration = registration; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
		public String getConnectorType() {
			return connectorType;
		}
		public void setConnectorType(String connectorType) {
			this.connectorType = connectorType;
		}
		public String getPortType() {
			return portType;
		}
		public void setPortType(String portType) {
			this.portType = portType;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getMobileNumber() {
			return mobileNumber;
		}
		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
		public String getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
		
    }

    public static class Coordinates {
        @JsonProperty("latitude")
        private String latitude;
        
        @JsonProperty("longitude")
        private String longitude;

        // Getters and setters
        public String getLatitude() { return latitude; }
        public void setLatitude(String latitude) { this.latitude = latitude; }
        
        public String getLongitude() { return longitude; }
        public void setLongitude(Double String) { this.longitude = longitude; }
    }
}