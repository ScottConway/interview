package com.lsq.conway.interview.domain;

import java.math.BigDecimal;

public class SupplierSummary {
    private String supplierId;
    private long totalInvoices = 0;
    private long openInvoices = 0;
    private long lateInvoices = 0;
    private BigDecimal totalOpenInvoiceAmount = BigDecimal.ZERO;
    private BigDecimal totalLateInvoiceAmount = BigDecimal.ZERO;

    public SupplierSummary(SupplierInvoice invoice) {
        supplierId = invoice.getSupplierId();
        updateTotals(invoice);
    }

    private void updateTotals(SupplierInvoice invoice) {
        totalInvoices++;
        if ("Open".equals(invoice.getStatus())) {
            openInvoices++;
            totalOpenInvoiceAmount = totalOpenInvoiceAmount.add(invoice.getInvoiceAmount().subtract(invoice.getPaymentAmount()));
        } else if ("Late".equals(invoice.getStatus())) {
            lateInvoices++;
            totalLateInvoiceAmount = totalLateInvoiceAmount.add(invoice.getInvoiceAmount());
        }
    }

    public void addInvoice(SupplierInvoice invoice) {
        updateTotals(invoice);
    }

    public String getSupplierId() {
        return supplierId;
    }

    public long getTotalNumberOfInvoices() {
        return totalInvoices;
    }

    public long getTotalNumberOfOpenInvoices() {
        return openInvoices;
    }

    public long getTotalNumberOfLateInvoices() {
        return lateInvoices;
    }

    public BigDecimal getTotalOpenInvoiceAmount() {
        return totalOpenInvoiceAmount;
    }

    public BigDecimal getTotalLateInvoiceAmount() {
        return totalLateInvoiceAmount;
    }


}
