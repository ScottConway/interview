package com.lsq.conway.interview.controller;

import com.lsq.conway.interview.domain.SupplierInvoice;
import com.lsq.conway.interview.exceptions.DuplicateEntriesException;
import com.lsq.conway.interview.service.SupplierInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class SupplierInvoiceController {
    @Autowired
    private SupplierInvoiceService service;

    @GetMapping("")
    public ResponseEntity<List<SupplierInvoice>> all() {
        List<SupplierInvoice> supplierInvoices = service.findAll();
        return new ResponseEntity<>(supplierInvoices, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            service.upload(file.getInputStream());
        } catch (IOException e) {
            return new ResponseEntity<>("Issue uploading data from file: " + e.getMessage(), HttpStatus.CONFLICT);
        } catch (DuplicateEntriesException e) {
            return new ResponseEntity<>("Duplicate data entries - supplier id and invoice id must be unique.\n\n Duplicate IDs:\n" + e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("file uploaded", HttpStatus.OK);
    }
}
