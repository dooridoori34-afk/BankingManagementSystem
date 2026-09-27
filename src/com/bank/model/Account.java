package com.bank.model;

public class Account {

    private long accountNumber;
    private int customerId;
    private String accountType;
    private double balance;

    public Account(long accountNumber, int customerId,
                   String accountType, double balance) {

        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {

        if (amount <= balance) {
            balance -= amount;
            return true;
        }

        return false;
    }
}