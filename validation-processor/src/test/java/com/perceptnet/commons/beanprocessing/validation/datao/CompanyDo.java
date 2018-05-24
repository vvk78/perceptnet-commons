package com.perceptnet.commons.beanprocessing.validation.datao;

import javax.persistence.Column;
import java.sql.Date;

/**
 * created by vkorovkin on 21.05.2018
 */
public class CompanyDo extends BaseDo {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_on")
    private Date createdOn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
