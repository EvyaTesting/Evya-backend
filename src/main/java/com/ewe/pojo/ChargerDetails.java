package com.ewe.pojo;
	
	import java.util.ArrayList;
	
	
	
	import java.util.ArrayList;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Set;
	
	import javax.persistence.CascadeType;
	import javax.persistence.Entity;
	import javax.persistence.FetchType;
	import javax.persistence.JoinColumn;
	import javax.persistence.ManyToOne;
	import javax.persistence.OneToMany;
	import javax.persistence.Table;
	
	import org.hibernate.annotations.Fetch;
	import org.hibernate.annotations.FetchMode;
	
	import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
	import com.fasterxml.jackson.annotation.JsonManagedReference;
	import com.fasterxml.jackson.annotation.JsonProperty;
	
	@Entity
	@Table(name = "charger_details")
	@JsonIgnoreProperties(ignoreUnknown = true, value = { "manufacturerDetails" , "hibernateLazyInitializer", "handler" })
	public class ChargerDetails extends BaseEntity {
	
	    private static final long serialVersionUID = 1L;
	
	    private String chargerType;
	    private Double totalCapacityKW;
	    private ManufacturerDetails manufacturerDetails;
	    private List<ConnectorDetails> chargingPort = new ArrayList<>();
	
	    private Set<Station> station = new HashSet<Station>(0);
	    private String currentType;
	    private int portQuantity;
	
	    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "chargerdetails")
	    @JsonManagedReference // manages serialization of Station collection
	    public Set<Station> getStation() {
	        return station;
	    }
	
	    public void setStation(Set<Station> station) {
	        this.station = station;
	    }
	
	    @JsonProperty
	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "manufacturer_id")
	    public ManufacturerDetails getManufacturerDetails() {
	        return manufacturerDetails;
	    }
	
	    public void setManufacturerDetails(ManufacturerDetails manufacturerDetails) {
	        this.manufacturerDetails = manufacturerDetails;
	    }
	
	    @JsonProperty
	    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "chargingStation")
	    @Fetch(value = FetchMode.SELECT)
	    public List<ConnectorDetails> getChargingPort() {
	        return chargingPort;
	    }
	
	    public void setChargingPort(List<ConnectorDetails> chargingPort) {
	        this.chargingPort = chargingPort;
	    }
	
	    public String getChargerType() {
	        return chargerType;
	    }
	
	    public void setChargerType(String chargerType) {
	        this.chargerType = chargerType;
	    }
	
	    public String getCurrentType() {
			return currentType;
		}
	
		public void setCurrentType(String currentType) {
			this.currentType = currentType;
		}
	
		public int getPortQuantity() {
			return portQuantity;
		}
	
		public void setPortQuantity(int i) {
			this.portQuantity = i;
		}
	
		public Double getTotalCapacityKW() {
	        return totalCapacityKW;
	    }
	
	    public void setTotalCapacityKW(Double totalCapacityKW) {
	        this.totalCapacityKW = totalCapacityKW;
	    }	
	}