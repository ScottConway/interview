package com.lsq.conway.interview.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.lsq.conway.interview.domain.SupplierInvoiceTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplierSummaryTest {

    @DisplayName("Create summary with open invoice.")
    @Test
    public void createWithOpen() {
        SupplierSummary summary = new SupplierSummary(OPEN_INVOICE1);
        assertEquals(OPEN_INVOICE1.getSupplierId(), summary.getSupplierId());
        assertEquals(1, summary.getTotalNumberOfInvoices());
        assertEquals(1, summary.getTotalNumberOfOpenInvoices());
        assertEquals(0, summary.getTotalNumberOfLateInvoices());
        assertEquals(OPEN_INVOICE1.getInvoiceAmount(), summary.getTotalOpenInvoiceAmount());
        assertEquals(OPEN_INVOICE1.getInvoiceAmount(), summary.getTotalOpenInvoiceAmount());
        assertEquals(BigDecimal.ZERO, summary.getTotalLateInvoiceAmount());
    }

    @DisplayName("Create summary with open invoice that has a payment.")
    @Test
    public void createWithOpenHasPayment() {
        SupplierSummary summary = new SupplierSummary(OPEN_INVOICE2);
        assertEquals(OPEN_INVOICE2.getSupplierId(), summary.getSupplierId());
        assertEquals(1, summary.getTotalNumberOfInvoices());
        assertEquals(1, summary.getTotalNumberOfOpenInvoices());
        assertEquals(0, summary.getTotalNumberOfLateInvoices());
        assertEquals(OPEN_INVOICE2.getInvoiceAmount().subtract(OPEN_INVOICE2.getPaymentAmount()), summary.getTotalOpenInvoiceAmount());
        assertEquals(BigDecimal.ZERO, summary.getTotalLateInvoiceAmount());
    }

    @DisplayName("Create summary with late invoice")
    @Test
    public void createWithLate() {
        SupplierSummary summary = new SupplierSummary(LATE_INVOICE1);
        assertEquals(LATE_INVOICE1.getSupplierId(), summary.getSupplierId());
        assertEquals(1, summary.getTotalNumberOfInvoices());
        assertEquals(0, summary.getTotalNumberOfOpenInvoices());
        assertEquals(1, summary.getTotalNumberOfLateInvoices());
        assertEquals(BigDecimal.ZERO, summary.getTotalOpenInvoiceAmount());
        assertEquals(LATE_INVOICE1.getInvoiceAmount(), summary.getTotalLateInvoiceAmount());
    }

    @DisplayName("Create summary and add many invoices of different types.")
    @Test
    public void addInvoices() {
        BigDecimal expected_total_open_amount = OPEN_INVOICE1.getInvoiceAmount().add(OPEN_INVOICE2.getInvoiceAmount())
                .subtract(OPEN_INVOICE1.getPaymentAmount()).subtract(OPEN_INVOICE2.getPaymentAmount());
        SupplierSummary summary = new SupplierSummary(OPEN_INVOICE1);
        summary.addInvoice(OPEN_INVOICE2);
        summary.addInvoice(LATE_INVOICE1);
        summary.addInvoice(CLOSED_INVOICE1);
        summary.addInvoice(SCHEDULED_INVOICE1);
        assertEquals(5, summary.getTotalNumberOfInvoices());
        assertEquals(2, summary.getTotalNumberOfOpenInvoices());
        assertEquals(1, summary.getTotalNumberOfLateInvoices());
        assertEquals(expected_total_open_amount, summary.getTotalOpenInvoiceAmount());
        assertEquals(LATE_INVOICE1.getInvoiceAmount(), summary.getTotalLateInvoiceAmount());
    }
}
