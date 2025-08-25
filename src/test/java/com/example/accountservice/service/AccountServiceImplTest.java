package com.example.accountservice.service;

import com.example.accountservice.dto.AccountRequest;
import com.example.accountservice.dto.TransactionRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.model.TransactionType;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountServiceImpl accountService;

    @BeforeEach
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        accountService = new AccountServiceImpl(accountRepository, transactionRepository);
    }

    @Test
    public void testCreateAccount() {
        AccountRequest req = new AccountRequest();
        req.setAccountName("Test");
        req.setCurrency("USD");
        req.setInitialBalance(new BigDecimal("100.00"));

        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        var resp = accountService.createAccount(req);
        assertNotNull(resp.getAccountId());
        assertEquals(new BigDecimal("100.00"), resp.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testCredit() {
        Account a = new Account();
        a.setAccountId(UUID.randomUUID().toString());
        a.setAccountName("A");
        a.setCurrency("USD");
        a.setBalance(new BigDecimal("50.00"));
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());

        when(accountRepository.findById(a.getAccountId())).thenReturn(Optional.of(a));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        TransactionRequest req = new TransactionRequest(TransactionType.CREDIT, new BigDecimal("25.00"), "desc");

        var resp = accountService.performTransaction(a.getAccountId(), req);
        assertEquals(new BigDecimal("75.00"), resp.getUpdatedBalance());
    }

    @Test
    public void testDebitInsufficient() {
        Account a = new Account();
        a.setAccountId(UUID.randomUUID().toString());
        a.setAccountName("A");
        a.setCurrency("USD");
        a.setBalance(new BigDecimal("10.00"));
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());

        when(accountRepository.findById(a.getAccountId())).thenReturn(Optional.of(a));

        TransactionRequest req = new TransactionRequest(TransactionType.DEBIT, new BigDecimal("20.00"), "desc");
        assertThrows(RuntimeException.class, () -> accountService.performTransaction(a.getAccountId(), req));
    }
}
