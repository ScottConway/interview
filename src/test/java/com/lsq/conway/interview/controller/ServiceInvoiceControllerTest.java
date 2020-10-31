package com.lsq.conway.interview.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ServiceInvoiceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Upload a file of customers.")
    @Test
    public void uploadFile() throws Exception {
        MockMultipartFile mockMultiPartFile = new MockMultipartFile("file", "dummy.csv",
                "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/upload");

        MvcResult result = mockMvc.perform(builder.file(mockMultiPartFile)).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("file uploaded", response.getContentAsString());
    }
}
