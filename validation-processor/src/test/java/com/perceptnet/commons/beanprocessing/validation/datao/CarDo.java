package com.perceptnet.commons.beanprocessing.validation.datao;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

/**
 * created by vkorovkin on 21.05.2018
 */
public class CarDo extends BaseDo {
    @Column(name = "vin", nullable = false)
    private String vin;

    @Column(name = "manufactured_at", nullable = false)
    private Timestamp manufacturedAt;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "car_model_id")
    private CarModelDo carModel;

    public CarModelDo getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModelDo carModel) {
        this.carModel = carModel;
    }

    public Timestamp getManufacturedAt() {
        return manufacturedAt;
    }

    public void setManufacturedAt(Timestamp manufacturedAt) {
        this.manufacturedAt = manufacturedAt;
    }
}
