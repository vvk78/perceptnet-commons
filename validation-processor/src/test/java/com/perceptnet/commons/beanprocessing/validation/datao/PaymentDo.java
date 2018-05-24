package com.perceptnet.commons.beanprocessing.validation.datao;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * created by vkorovkin on 21.05.2018
 */
public class PaymentDo extends BaseDo {
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "invoice_id")
    private InvoiceDo invoice;

    public InvoiceDo getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDo invoice) {
        this.invoice = invoice;
    }
}
