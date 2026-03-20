package com.ewe.form;






import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PortStatusResponse {
    private Long portId;
    private String portName;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Asia/Kolkata")
    private Date currentTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Asia/Kolkata")
    private Date reservationStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Asia/Kolkata")
    private Date reservationEndTime;
    private String durationText;
    private Long durationHours;
    public Date getReservationStartTime() {
		return reservationStartTime;
	}

	public void setReservationStartTime(Date reservationStartTime) {
		this.reservationStartTime = reservationStartTime;
	}

	private Long durationMinutes;
   
    private Double calculatedAmount;
    private Double unitsConsumed;
    private String billingUnits;
    private Double max_power_kW;

    private Double billingAmount;
    public Double getBillingAmount() {
		return billingAmount;
	}

	public void setBillingAmount(Double billingAmount) {
		this.billingAmount = billingAmount;
	}

	// Constructors, Getters and Setters
    public PortStatusResponse() {}
    
    public PortStatusResponse(Long portId, String portName, String status) {
        this.portId = portId;
        this.portName = portName;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getPortId() { return portId; }
    public void setPortId(Long portId) { this.portId = portId; }
    
    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getCurrentTime() { return currentTime; }
    public void setCurrentTime(Date currentTime) { this.currentTime = currentTime; }
    
    public Date getReservationEndTime() { return reservationEndTime; }
    public void setReservationEndTime(Date reservationEndTime) { this.reservationEndTime = reservationEndTime; }
    
    public String getDurationText() { return durationText; }
    public void setDurationText(String durationText) { this.durationText = durationText; }
    
    public Long getDurationHours() { return durationHours; }
    public void setDurationHours(Long durationHours) { this.durationHours = durationHours; }
    
    public Long getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; }
    
   
    
    public Double getCalculatedAmount() { return calculatedAmount; }
    public void setCalculatedAmount(Double calculatedAmount) { this.calculatedAmount = calculatedAmount; }
    
    public Double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(Double unitsConsumed) { this.unitsConsumed = unitsConsumed; }
    
    public String getBillingUnits() { return billingUnits; }
    public void setBillingUnits(String billingUnits) { this.billingUnits = billingUnits; }

	public Double getMax_power_kW() {
		return max_power_kW;
	}

	public void setMax_power_kW(Double max_power_kW) {
		this.max_power_kW = max_power_kW;
	}

	

	
}