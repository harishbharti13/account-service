package com.example.accountservice.repository;

import com.example.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountAccountIdOrderByTimestampDesc(String accountId);
    List<Transaction> findByAccountAccountIdAndTimestampBetweenOrderByTimestampDesc(String accountId, Instant start, Instant end);
}
