package com.perceptnet.commons.beanprocessing.validation.datao;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin on 21.05.2018
 */
public class InvoiceDo extends BaseDo {

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "order_id")
    private OrderDo order;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<PaymentDo> payments = new ArrayList<>();

    public OrderDo getOrder() {
        return order;
    }

    public void setOrder(OrderDo order) {
        this.order = order;
    }

    public List<PaymentDo> getPayments() {
        return payments;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
