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
public class OrderDo extends BaseDo {
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "quote_id")
    private QuoteDo quote;

    @Column(name = "customerName", nullable = false)
    private String customerName;

    @Column(name = "deadlineAt", nullable = false)
    private Timestamp deadlineAt;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<InvoiceDo> invoices = new ArrayList<>();

    public QuoteDo getQuote() {
        return quote;
    }

    public void setQuote(QuoteDo quote) {
        this.quote = quote;
    }

    public List<InvoiceDo> getInvoices() {
        return invoices;
    }
}
