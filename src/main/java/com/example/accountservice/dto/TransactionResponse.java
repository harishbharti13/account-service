package com.example.accountservice.dto;

import com.example.accountservice.model.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {
    private String transactionId;
    private String accountId;
    private TransactionType type;
    private BigDecimal amount;
    private Instant timestamp;
    private String description;
    private BigDecimal updatedBalance;

    // getters and setters

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUpdatedBalance() {
        return updatedBalance;
    }

    public void setUpdatedBalance(BigDecimal updatedBalance) {
        this.updatedBalance = updatedBalance;
    }
}
