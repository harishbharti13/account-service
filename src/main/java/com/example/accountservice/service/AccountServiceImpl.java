package com.example.accountservice.service;

import com.example.accountservice.dto.*;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.exception.InsufficientFundsException;
import com.example.accountservice.exception.NotFoundException;
import com.example.accountservice.model.TransactionType;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {
        Account a = new Account();
        a.setAccountId(UUID.randomUUID().toString());
        a.setAccountName(request.getAccountName());
        a.setCurrency(request.getCurrency());
        BigDecimal bal = request.getInitialBalance() == null ? BigDecimal.ZERO : request.getInitialBalance();
        bal = bal.setScale(2, RoundingMode.HALF_EVEN);
        a.setBalance(bal);
        Instant now = Instant.now();
        a.setCreatedAt(now);
        a.setUpdatedAt(now);
        accountRepository.save(a);
        return toResponse(a);
    }

    @Override
    public AccountResponse getAccount(String accountId) {
        Account a = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(a);
    }

    @Override
    @Transactional
    public TransactionResponse performTransaction(String accountId, TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_EVEN);
        if (request.getType() == TransactionType.DEBIT) {
            BigDecimal newBal = account.getBalance().subtract(amount);
            if (newBal.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException(accountId, amount);
            }
            account.setBalance(newBal);
        } else {
            account.setBalance(account.getBalance().add(amount));
        }
        account.setUpdatedAt(Instant.now());
        try {
            accountRepository.save(account);
        } catch (OptimisticLockingFailureException e) {
            throw e;
        }

        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setAccount(account);
        tx.setType(request.getType());
        tx.setAmount(amount);
        tx.setTimestamp(Instant.now());
        tx.setDescription(request.getDescription());
        transactionRepository.save(tx);

        TransactionResponse resp = response(tx, account.getBalance());
        return resp;
    }

    @Override
    public List<TransactionResponse> getTransactions(String accountId, Instant start, Instant end) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        List<Transaction> txs;
        if (start != null && end != null) {
            txs = transactionRepository.findByAccountAccountIdAndTimestampBetweenOrderByTimestampDesc(accountId, start, end);
        } else {
            txs = transactionRepository.findByAccountAccountIdOrderByTimestampDesc(accountId);
        }
        return txs.stream().map(t -> response(t, account.getBalance())).collect(Collectors.toList());
    }

    private AccountResponse toResponse(Account a) {
        AccountResponse r = new AccountResponse();
        r.setAccountId(a.getAccountId());
        r.setAccountName(a.getAccountName());
        r.setCurrency(a.getCurrency());
        r.setBalance(a.getBalance());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }

    private TransactionResponse response(Transaction tx, BigDecimal updatedBalance) {
        TransactionResponse r = new TransactionResponse();
        r.setTransactionId(tx.getTransactionId());
        r.setAccountId(tx.getAccount().getAccountId());
        r.setType(tx.getType());
        r.setAmount(tx.getAmount());
        r.setTimestamp(tx.getTimestamp());
        r.setDescription(tx.getDescription());
        r.setUpdatedBalance(updatedBalance);
        return r;
    }
}
