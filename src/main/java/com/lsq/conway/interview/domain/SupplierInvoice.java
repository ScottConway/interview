package com.lsq.conway.interview.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class SupplierInvoice {
    @CsvBindByName(column = "Supplier Id")
    private String supplierId;
    @CsvBindByName(column = "Invoice Id")
    private String invoiceId;
    @CsvBindByName(column = "Invoice Date")
    @CsvDate("yyyy-MM-dd")
    private Date invoiceDate;
    @CsvBindByName(column = "Invoice Amount")
    private BigDecimal invoiceAmount;
    @CsvBindByName(column = "Terms")
    private int terms;
    @CsvBindByName(column = "Payment Date")
    @CsvDate("yyyy-MM-dd")
    private Date paymentDate;
    @CsvBindByName(column = "Payment Amount")
    private BigDecimal paymentAmount;

    public SupplierInvoice() {
    }

    public SupplierInvoice(String supplierId, String invoiceId, Date invoiceDate, BigDecimal invoiceAmount, int terms, Date paymentDate, BigDecimal paymentAmount) {
        this.supplierId = supplierId;
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.invoiceAmount = invoiceAmount;
        this.terms = terms;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public int getTerms() {
        return terms;
    }

    public void setTerms(int terms) {
        this.terms = terms;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount == null ? BigDecimal.ZERO : paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierInvoice)) return false;
        SupplierInvoice that = (SupplierInvoice) o;
        return terms == that.terms &&
                supplierId.equals(that.supplierId) &&
                invoiceId.equals(that.invoiceId) &&
                invoiceDate.equals(that.invoiceDate) &&
                invoiceAmount.equals(that.invoiceAmount) &&
                Objects.equals(paymentDate, that.paymentDate) &&
                getPaymentAmount().compareTo(that.getPaymentAmount()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, invoiceId, invoiceDate, invoiceAmount, terms, paymentDate, getPaymentAmount());
    }

    public String getStatus() {
        BigDecimal paid = getPaymentAmount();
        ZonedDateTime d = ZonedDateTime.ofInstant(invoiceDate.toInstant(),
                ZoneId.systemDefault()).plusDays(terms);

        if (paid.compareTo(BigDecimal.ZERO) > 0 && hasFuturePaymentDate()) {
            return "Payment Scheduled";
        } else if (invoiceAmount.compareTo(paid) <= 0) {
            return "Closed";
        } else {
            return d.compareTo(ZonedDateTime.now()) >= 0 ? "Open" : "Late";
        }

    }

    private boolean hasFuturePaymentDate() {
        if (paymentDate == null) {
            return false;
        }
        ZonedDateTime pd = ZonedDateTime.ofInstant(paymentDate.toInstant(), ZoneId.systemDefault());

        return pd.isAfter(ZonedDateTime.now());
    }
}
