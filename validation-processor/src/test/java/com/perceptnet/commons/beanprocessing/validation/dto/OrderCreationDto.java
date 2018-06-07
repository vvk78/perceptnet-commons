package com.perceptnet.commons.beanprocessing.validation.dto;

import com.perceptnet.abstractions.Updatable;

import java.util.Date;


/**
 * created by vkorovkin on 21.05.2018
 */
public class OrderCreationDto implements Updatable {
    private String customerName;
    private Date deadlineAt;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getDeadlineAt() {
        return deadlineAt;
    }

    public void setDeadlineAt(Date deadlineAt) {
        this.deadlineAt = deadlineAt;
    }
}
