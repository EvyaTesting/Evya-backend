package com.ewe.pojo;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;





import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ev_variants")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "model" })
public class EV_Variants extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String variantName;
    private EV_Models model;
    private String chargerType;
    private String chargingOptions;
    private Double batterycapacity;
    private String rangeKm;
    private String power;

    @Column(name = "variant_name", nullable = false)
    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    @Column(name = "charger_type")
    public String getChargerType() {
		return chargerType;
	}

	public void setChargerType(String chargerType) {
		this.chargerType = chargerType;
	}

    @Column(name = "charging_options")
	public String getChargingOptions() {
		return chargingOptions;
	}

	public void setChargingOptions(String chargingOptions) {
		this.chargingOptions = chargingOptions;
	}

	@Column(name = "battery_capacity_kwh")
	public Double getBatterycapacity() {
		return batterycapacity;
	}

	public void setBatterycapacity(Double batterycapacity) {
		this.batterycapacity = batterycapacity;
	}

	@Column(name = "range_km")
	public String getRangeKm() {
		return rangeKm;
	}

	public void setRangeKm(String rangeKm) {
		this.rangeKm = rangeKm;
	}

	@Column(name = "power")
	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = EV_Models.class)
    @JoinColumn(name = "model_id")
    public EV_Models getModel() {
        return model;
    }

    public void setModel(EV_Models model) {
        this.model = model;
    }
}
