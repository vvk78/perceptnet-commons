package com.perceptnet.commons.beanprocessing.validation.datao;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

/**
 * created by vkorovkin on 18.05.2018
 */
public class CarModelDo extends BaseDo {
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "manufacturer_id")
    private CompanyDo manufacturer;

    @Column(name = "sales_started_at")
    private Timestamp salesStartedAt;

    @Column(name = "sales_started_at")
    private Timestamp salesFinishedAt;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompanyDo getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(CompanyDo manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Timestamp getSalesStartedAt() {
        return salesStartedAt;
    }

    public void setSalesStartedAt(Timestamp salesStartedAt) {
        this.salesStartedAt = salesStartedAt;
    }

    public Timestamp getSalesFinishedAt() {
        return salesFinishedAt;
    }

    public void setSalesFinishedAt(Timestamp salesFinishedAt) {
        this.salesFinishedAt = salesFinishedAt;
    }
}
