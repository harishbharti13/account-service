package com.example.accountservice.service;

import com.example.accountservice.dto.AccountRequest;
import com.example.accountservice.dto.AccountResponse;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.dto.TransactionResponse;

import java.time.Instant;
import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest request);
    AccountResponse getAccount(String accountId);
    TransactionResponse performTransaction(String accountId, TransactionRequest request);
    List<TransactionResponse> getTransactions(String accountId, Instant start, Instant end);
}
