package com.ewe.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "month", uniqueConstraints = @UniqueConstraint(columnNames = "monthName"))
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class Month extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "monthName", nullable = false, length = 20)
    private String monthName;

    public String getMonthName() {
        return monthName;
    }
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
}
