package com.lsq.conway.interview.service;

import com.lsq.conway.interview.domain.SupplierInvoice;
import com.lsq.conway.interview.domain.SupplierSummary;
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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.lsq.conway.interview.domain.SupplierInvoiceTest.*;
import static org.junit.jupiter.api.Assertions.*;
        
@SpringBootTest
@ActiveProfiles("development")
public class SupplierInvoiceServiceTest {
    private static final String FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT);


    private static final String UPLOAD = String.format("Supplier Id,Invoice Id,Invoice Date,Invoice Amount,Terms,Payment Date,Payment Amount\n" +
                    "%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,%s,%s\n",
            OPEN_INVOICE1.getSupplierId(), OPEN_INVOICE1.getInvoiceId(), DATE_FORMAT.format(OPEN_INVOICE1.getInvoiceDate()), OPEN_INVOICE1.getInvoiceAmount(), OPEN_INVOICE1.getTerms(),
            OPEN_INVOICE2.getSupplierId(), OPEN_INVOICE2.getInvoiceId(), DATE_FORMAT.format(OPEN_INVOICE2.getInvoiceDate()), OPEN_INVOICE2.getInvoiceAmount(), OPEN_INVOICE2.getTerms(), DATE_FORMAT.format(OPEN_INVOICE2.getPaymentDate()), OPEN_INVOICE2.getPaymentAmount());

    private static final String UPLOAD_UPDATE = String.format("Supplier Id,Invoice Id,Invoice Date,Invoice Amount,Terms,Payment Date,Payment Amount\n" +
                    "%s,%s,%s,%s,%d,%s,%s\n%s,%s,%s,%s,%d,,\n",
            OPEN_INVOICE2.getSupplierId(), OPEN_INVOICE2.getInvoiceId(), DATE_FORMAT.format(OPEN_INVOICE2.getInvoiceDate()), OPEN_INVOICE2.getInvoiceAmount(), OPEN_INVOICE2.getTerms(), DATE_FORMAT.format(OPEN_INVOICE2.getPaymentDate()), OPEN_INVOICE2.getPaymentAmount(),
            LATE_INVOICE1.getSupplierId(), LATE_INVOICE1.getInvoiceId(), DATE_FORMAT.format(LATE_INVOICE1.getInvoiceDate()), LATE_INVOICE1.getInvoiceAmount(), LATE_INVOICE1.getTerms());

    private static final String UPLOAD_WITH_DUPLICATES = String.format("Supplier Id,Invoice Id,Invoice Date,Invoice Amount,Terms,Payment Date,Payment Amount\n" +
                    "%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,,\n%s,%s,%s,%s,%d,,\n",
            OPEN_INVOICE1.getSupplierId(), OPEN_INVOICE1.getInvoiceId(), DATE_FORMAT.format(OPEN_INVOICE1.getInvoiceDate()), OPEN_INVOICE1.getInvoiceAmount(), OPEN_INVOICE1.getTerms(),
            LATE_INVOICE1.getSupplierId(), LATE_INVOICE1.getInvoiceId(), DATE_FORMAT.format(LATE_INVOICE1.getInvoiceDate()), LATE_INVOICE1.getInvoiceAmount(), LATE_INVOICE1.getTerms(),
            OPEN_INVOICE1.getSupplierId(), OPEN_INVOICE1.getInvoiceId(), DATE_FORMAT.format(OPEN_INVOICE1.getInvoiceDate()), OPEN_INVOICE1.getInvoiceAmount(), OPEN_INVOICE1.getTerms(),
            LATE_INVOICE1.getSupplierId(), LATE_INVOICE1.getInvoiceId(), DATE_FORMAT.format(LATE_INVOICE1.getInvoiceDate()), LATE_INVOICE1.getInvoiceAmount(), LATE_INVOICE1.getTerms());

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
            assertTrue(message.contains(buildMessage(OPEN_INVOICE1.getSupplierId(), OPEN_INVOICE1.getInvoiceId())));
            assertTrue(message.contains(buildMessage(LATE_INVOICE1.getSupplierId(), LATE_INVOICE1.getInvoiceId())));
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
        assertTrue(invoices.contains(OPEN_INVOICE1));
        assertTrue(invoices.contains(OPEN_INVOICE2));

        supplierInvoiceService.upload(UPLOAD_UPDATE_INPUT_STREAM);
        invoices = supplierInvoiceService.findAll();
        assertEquals(3, invoices.size());
        assertTrue(invoices.contains(OPEN_INVOICE1));
        assertTrue(invoices.contains(OPEN_INVOICE2));
        assertTrue(invoices.contains(LATE_INVOICE1));
    }

    @DisplayName("list Summary")
    @Test
    public void listSummary() throws DuplicateEntriesException {
        supplierInvoiceService.upload(UPLOAD_INPUT_STREAM);
        List<SupplierSummary> summaryList = supplierInvoiceService.listSupplierSummary();
        assertEquals(1, summaryList.size());
    }

    private CharSequence buildMessage(String supplierId, String invoiceId) {
        return String.format("Supplier Id: %s Invoice Id: %s", supplierId, invoiceId);
    }
}
