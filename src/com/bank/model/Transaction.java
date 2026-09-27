package com.bank.model;

import java.time.LocalDateTime;

public class Transaction {

    private int transactionId;
    private long accountNumber;
    private String transactionType;
    private double amount;
    private LocalDateTime transactionDate;

    public Transaction(int transactionId, long accountNumber,
                       String transactionType, double amount) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
    }

    public int getTransactionId() {
        return transactionId;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}