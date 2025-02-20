package com.banking.model;

import java.math.BigDecimal;

public abstract class Account {
    private final String accountNumber;
    private BigDecimal balance;
    private final AccountType accountType;

    public Account(String accountNumber, BigDecimal balance, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            balance = balance.add(amount);
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
    }

    public void withdraw(BigDecimal amount) {
        if (canWithdraw(amount)) {
            balance = balance.subtract(amount);
        } else {
            throw new IllegalArgumentException("Insufficient funds or withdrawal not allowed.");
        }
    }

    protected abstract boolean canWithdraw(BigDecimal amount);

    public abstract void processMonthlyFees();

    @Override
    public String toString() {
        return String.format("%s Account [%s] - Balance: $%.2f",
                accountType, accountNumber, balance);
    }
}
