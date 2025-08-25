package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        AccountResponse resp = accountService.createAccount(request);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId) {
        AccountResponse resp = accountService.getAccount(accountId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> performTransaction(@PathVariable String accountId,
                                                                  @Valid @RequestBody TransactionRequest request) {
        TransactionResponse resp = accountService.performTransaction(accountId, request);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @PathVariable String accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {

        List<TransactionResponse> resp = accountService.getTransactions(accountId, startDate, endDate);
        return ResponseEntity.ok(resp);
    }
}
