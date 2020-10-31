package com.lsq.conway.interview.service;

import com.lsq.conway.interview.domain.SupplierInvoice;
import com.lsq.conway.interview.exceptions.DuplicateEntriesException;
import com.lsq.conway.interview.mapper.SupplierInvoiceMapper;
import com.lsq.conway.interview.mapper.TempSupplierInvoiceMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SupplierInvoiceService {

    @Autowired
    private SupplierInvoiceMapper mapper;
    @Autowired
    private TempSupplierInvoiceMapper tempMapper;

    public void upload(InputStream inputStream) throws DuplicateEntriesException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        StringBuilder duplicates = new StringBuilder(1024);

        try {

            MappingStrategy<SupplierInvoice> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
            mappingStrategy.setType(SupplierInvoice.class);

            CsvToBeanBuilder<SupplierInvoice> builder = new CsvToBeanBuilder<>(reader);
            CsvToBean<SupplierInvoice> beans = builder.withMappingStrategy(mappingStrategy).build();

            Iterator<SupplierInvoice> iterator = beans.iterator();
            while (iterator.hasNext()) {
                SupplierInvoice invoice = iterator.next();
                if (tempMapper.find(invoice.getSupplierId(), invoice.getInvoiceId()) == null) {
                    tempMapper.insertSupplierInvoice(invoice);
                } else {
                    duplicates.append(String.format("Supplier Id: %s Invoice Id: %s\n", invoice.getSupplierId(), invoice.getInvoiceId()));
                }
            }
            if (duplicates.length() > 0) {
                throw new DuplicateEntriesException(duplicates.toString());
            } else {
                // okay messed up here - I was trying to be cute and save memory with a two table solution because I did not know how big
                // the files could be.  But am having issues with the sql in mybatis so with less than two hours to go I am doing brute force.
                // you can see where I was heading by looking at the commented persistData sql in the TempSupplierInvoice.xml
                List<SupplierInvoice> invoices = tempMapper.findAll();
                for (SupplierInvoice invoice : invoices) {
                    if (mapper.find(invoice.getSupplierId(), invoice.getInvoiceId()) == null) {
                        mapper.insertSupplierInvoice(invoice);
                    } else {
                        mapper.updateSupplierInvoice(invoice);
                    }
                }
            }
        } finally {
            tempMapper.deleteAll();
        }
    }

    public List<SupplierInvoice> findAll() {
        return mapper.findAll();
    }
}
