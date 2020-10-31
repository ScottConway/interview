package com.lsq.conway.interview.domain;

import java.math.BigDecimal;

public class SupplierSummary {
    private String supplierId;
    private long totalInvoices;
    private long openInvoices;
    private long lateInvoices;
    private BigDecimal totalOpenInvoiceAmount;
    private BigDecimal totalLateInvoiceAmount;

    public SupplierSummary(SupplierInvoice invoice) {
        supplierId = invoice.getSupplierId();
        totalInvoices = 1;
    }
}
