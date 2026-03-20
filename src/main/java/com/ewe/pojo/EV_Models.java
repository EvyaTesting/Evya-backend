package com.ewe.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ev_models")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "brand" })
public class EV_Models extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String model;
    private EV_Brands brand;
    
    private Set<EV_Variants> variants = new HashSet<EV_Variants>(0);

    @OneToMany(mappedBy = "model", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<EV_Variants> getVariants() {
        return variants;
    }

    public void setVariants(Set<EV_Variants> variants) {
        this.variants = variants;
    }

    @Column(name = "model", nullable = false)
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = EV_Brands.class)
    @JoinColumn(name = "brand_id")
    public EV_Brands getBrand() {
        return brand;
    }
    public void setBrand(EV_Brands brand) {
        this.brand = brand;
    }
}
