package com.lsq.conway.interview.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplierInvoiceTest {

    private static final String FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT);
    private static final BigDecimal AMOUNT1 = BigDecimal.valueOf(3547.89);
    private static final BigDecimal AMOUNT2 = BigDecimal.valueOf(200.00);
    private static final Date DATE_PLUS_60 = new Date(LocalDate.now().plusDays(60).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    private static final Date DATE_PLUS_20 = new Date(LocalDate.now().plusDays(20).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    private static final Date DATE_PLUS_10 = new Date(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    private static final Date DATE_MINUS_10 = new Date(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    private static final Date DATE_MINUS_20 = new Date(LocalDate.now().minusDays(20).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    public static final SupplierInvoice OPEN_INVOICE1 = new SupplierInvoice("supplier1", "invoiceid1", DATE_PLUS_20, AMOUNT1, 30, null, null);
    public static final SupplierInvoice OPEN_INVOICE2 = new SupplierInvoice("supplier1", "invoiceid2", DATE_PLUS_20, AMOUNT1, 30, DATE_MINUS_10, AMOUNT2);
    public static final SupplierInvoice SCHEDULED_INVOICE1 = new SupplierInvoice("supplier1", "invoiceid3", DATE_PLUS_20, AMOUNT1, 30, DATE_PLUS_10, AMOUNT2);
    public static final SupplierInvoice CLOSED_INVOICE1 = new SupplierInvoice("supplier1", "invoiceid4", DATE_PLUS_20, AMOUNT1, 30, DATE_MINUS_10, AMOUNT1);
    public static final SupplierInvoice LATE_INVOICE1 = new SupplierInvoice("supplier1", "invoiceid5", DATE_MINUS_20, AMOUNT1, 10, null, null);

    @DisplayName("Check status")
    @Test
    public void status() {
        assertEquals("Open", OPEN_INVOICE1.getStatus());
        assertEquals("Open", OPEN_INVOICE2.getStatus());
        assertEquals("Payment Scheduled", SCHEDULED_INVOICE1.getStatus());
        assertEquals("Late", LATE_INVOICE1.getStatus());
        assertEquals("Closed", CLOSED_INVOICE1.getStatus());
    }

    @DisplayName("getPaymentAmount will return zero if null")
    @Test
    public void getPaymentAmountWithNull() {
        assertEquals(BigDecimal.ZERO, OPEN_INVOICE1.getPaymentAmount());
    }
}
