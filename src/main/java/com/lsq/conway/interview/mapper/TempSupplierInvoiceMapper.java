package com.lsq.conway.interview.mapper;

import com.lsq.conway.interview.domain.SupplierInvoice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TempSupplierInvoiceMapper {
    void insertSupplierInvoice(SupplierInvoice supplierInvoice);
    List<SupplierInvoice> findAll();
    SupplierInvoice find(String supplierId, String invoiceId);
    void deleteAll();
}
