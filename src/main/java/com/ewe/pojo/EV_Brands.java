package com.ewe.pojo;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ev_brands")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class EV_Brands extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String brandName;
    private Set<EV_Models> models = new HashSet<EV_Models>(0);

    @Column(name = "brand_name", nullable = false, length = 50)
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @OneToMany(mappedBy = "brand", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<EV_Models> getModels() {
        return models;
    }
    public void setModels(Set<EV_Models> models) {
        this.models = models;
    }
}