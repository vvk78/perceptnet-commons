package com.perceptnet.commons.beanprocessing.validation.datao;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin on 21.05.2018
 */
public class QuoteDo extends BaseDo {
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "product_id")
    private ProductDo product;

    @OneToMany(mappedBy = "quote", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<OrderDo> orders = new ArrayList<>();

    public ProductDo getProduct() {
        return product;
    }

    public void setProduct(ProductDo product) {
        this.product = product;
    }

    public List<OrderDo> getOrders() {
        return orders;
    }
}
