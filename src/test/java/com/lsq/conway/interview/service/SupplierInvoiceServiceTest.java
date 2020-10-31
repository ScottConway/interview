package com.lsq.conway.interview.service;

import com.lsq.conway.interview.domain.SupplierInvoice;
import com.lsq.conway.interview.exceptions.DuplicateEntriesException;
import com.lsq.conway.interview.mapper.SupplierInvoiceMapper;
import com.lsq.conway.interview.mapper.TempSupplierInvoiceMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("development")
public class SupplierInvoiceServiceTest {
    private static final String FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT);
    private static final Date MAR312020 = new Date(new GregorianCalendar(2020, 3, 31).getTimeInMillis());
    private static final Date MAY052020 = new Date(new GregorianCalendar(2020, 5, 5).getTimeInMillis());
    private static final Date JULY012020 = new Date(new GregorianCalendar(2020, 7, 1).getTimeInMillis());
    private static final SupplierInvoice SI1 = new SupplierInvoice("supplier1", "invoiceid1", MAR312020, BigDecimal.valueOf(3547.88), 30, null, null);
    private static final SupplierInvoice SI1NEW = new SupplierInvoice("supplier1", "invoiceid1", MAR312020, BigDecimal.valueOf(3547.88), 30, MAY052020, BigDecimal.valueOf(3547.88));
    private static final SupplierInvoice SI2 = new SupplierInvoice("supplier2", "invoiceid2", MAY052020, BigDecimal.valueOf(1243.22), 120, JULY012020, BigDecimal.valueOf(1243.22));

    private static final String UPLOAD = String.format("Supplier Id,Invoice Id,Invoice Date,Invoice Amount,Terms,Payment Date,Payment Amount\n" +
                    "%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,%s,%s\n",
            SI1.getSupplierId(), SI1.getInvoiceId(), DATE_FORMAT.format(SI1.getInvoiceDate()), SI1.getInvoiceAmount(), SI1.getTerms(),
            SI2.getSupplierId(), SI2.getInvoiceId(), DATE_FORMAT.format(SI2.getInvoiceDate()), SI2.getInvoiceAmount(), SI2.getTerms(), DATE_FORMAT.format(SI2.getPaymentDate()), SI2.getPaymentAmount());

    private static final String UPLOAD_UPDATE = String.format("Supplier Id,Invoice Id,Invoice Date,Invoice Amount,Terms,Payment Date,Payment Amount\n" +
                    "%s,%s,%s,%s,%d,%s,%s\n%s,%s,%s,%s,%d,%s,%s\n",
            SI1NEW.getSupplierId(), SI1NEW.getInvoiceId(), DATE_FORMAT.format(SI1NEW.getInvoiceDate()), SI1NEW.getInvoiceAmount(), SI1NEW.getTerms(), DATE_FORMAT.format(SI1NEW.getPaymentDate()), SI1NEW.getPaymentAmount(),
            SI2.getSupplierId(), SI2.getInvoiceId(), DATE_FORMAT.format(SI2.getInvoiceDate()), SI2.getInvoiceAmount(), SI2.getTerms(), DATE_FORMAT.format(SI2.getPaymentDate()), SI2.getPaymentAmount());

    private static final String UPLOAD_WITH_DUPLICATES = String.format("Supplier Id,Invoice Id,Invoice Date,Invoice Amount,Terms,Payment Date,Payment Amount\n" +
                    "%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,%s,%s\n%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,%s,%s\n",
            SI1.getSupplierId(), SI1.getInvoiceId(), DATE_FORMAT.format(SI1.getInvoiceDate()), SI1.getInvoiceAmount(), SI1.getTerms(),
            SI2.getSupplierId(), SI2.getInvoiceId(), DATE_FORMAT.format(SI2.getInvoiceDate()), SI2.getInvoiceAmount(), SI2.getTerms(), DATE_FORMAT.format(SI2.getPaymentDate()), SI2.getPaymentAmount(),
            SI1.getSupplierId(), SI1.getInvoiceId(), DATE_FORMAT.format(SI1.getInvoiceDate()), SI1.getInvoiceAmount(), SI1.getTerms(),
            SI2.getSupplierId(), SI2.getInvoiceId(), DATE_FORMAT.format(SI2.getInvoiceDate()), SI2.getInvoiceAmount(), SI2.getTerms(), DATE_FORMAT.format(SI2.getPaymentDate()), SI2.getPaymentAmount());

    private static final InputStream UPLOAD_INPUT_STREAM = new ByteArrayInputStream(UPLOAD.getBytes(Charset.forName("UTF-8")));
    private static final InputStream UPLOAD_UPDATE_INPUT_STREAM = new ByteArrayInputStream(UPLOAD_UPDATE.getBytes(Charset.forName("UTF-8")));
    private static final InputStream UPLOAD_WITH_DUPLICATES_INPUT_STREAM = new ByteArrayInputStream(UPLOAD_WITH_DUPLICATES.getBytes(Charset.forName("UTF-8")));

    @Autowired
    private SupplierInvoiceService supplierInvoiceService;
    @Autowired
    private SupplierInvoiceMapper supplierInvoiceMapper;
    @Autowired
    private TempSupplierInvoiceMapper tempSupplierInvoiceMapper;

    private static String handleNull(Object o) {
        return o == null ? "" : o.toString();
    }

    @AfterEach
    public void clearData() throws IOException {
        supplierInvoiceMapper.deleteAll();
        UPLOAD_INPUT_STREAM.reset();
    }

    @DisplayName("Upload data to database")
    @Test
    public void upload() throws DuplicateEntriesException {
        supplierInvoiceService.upload(UPLOAD_INPUT_STREAM);
        List<SupplierInvoice> invoices = supplierInvoiceService.findAll();
        assertEquals(2, invoices.size());
    }

    @DisplayName("handle duplicate values in upload file")
    @Test
    public void uploadWithDuplicates() throws DuplicateEntriesException {
        try {
            supplierInvoiceService.upload(UPLOAD_WITH_DUPLICATES_INPUT_STREAM);
            fail("Expected to have an DuplicateEntries exception thrown.");
        }
        catch (DuplicateEntriesException e) {
            String message = e.getMessage();
            assertTrue(message.contains(buildMessage(SI1.getSupplierId(), SI1.getInvoiceId())));
            assertTrue(message.contains(buildMessage(SI2.getSupplierId(), SI2.getInvoiceId())));
            List<SupplierInvoice> invoices = supplierInvoiceService.findAll();
            assertEquals(0, invoices.size());
        } finally {
            List<SupplierInvoice> invoices = tempSupplierInvoiceMapper.findAll();
            assertEquals(0, invoices.size());
        }
    }

    @DisplayName("Multiple uploads will just overwrite existing data")
    @Test
    public void multipleUploads() throws DuplicateEntriesException {
        supplierInvoiceService.upload(UPLOAD_INPUT_STREAM);
        List<SupplierInvoice> invoices = supplierInvoiceService.findAll();
        assertEquals(2, invoices.size());
        assertTrue(invoices.contains(SI1));
        assertTrue(invoices.contains(SI2));

        supplierInvoiceService.upload(UPLOAD_UPDATE_INPUT_STREAM);
        invoices = supplierInvoiceService.findAll();
        assertEquals(2, invoices.size());
        assertTrue(invoices.contains(SI1NEW));
        assertTrue(invoices.contains(SI2));
    }

    private CharSequence buildMessage(String supplierId, String invoiceId) {
        return String.format("Supplier Id: %s Invoice Id: %s", supplierId, invoiceId);
    }
}
