package com.example.accountservice.controller;

import com.example.accountservice.dto.AccountRequest;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.model.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndTransactionFlow() throws Exception {
        AccountRequest req = new AccountRequest();
        req.setAccountName("IntTest");
        req.setCurrency("USD");
        req.setInitialBalance(new BigDecimal("100.00"));

        String createJson = objectMapper.writeValueAsString(req);

        String result = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(result).get("accountId").asText();

        TransactionRequest tx = new TransactionRequest(TransactionType.CREDIT, new BigDecimal("50.00"), "pay");
        mockMvc.perform(post("/api/v1/accounts/" + id + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tx)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.updatedBalance").value(150.00));

        TransactionRequest tx2 = new TransactionRequest(TransactionType.DEBIT, new BigDecimal("25.00"), "atm");
        mockMvc.perform(post("/api/v1/accounts/" + id + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tx2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.updatedBalance").value(125.00));
    }
}
