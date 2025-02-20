package com.banking.model;

import java.math.BigDecimal;

public class CheckingAccount extends Account implements Transferable {
    private static final BigDecimal MONTHLY_FEE = new BigDecimal("12.00");
    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("-100.00");
    private int monthlyTransactions;

    public CheckingAccount(String accountNumber, BigDecimal balance) {
        super(accountNumber, balance, AccountType.CHECKING); // ✅ Use AccountType Enum
        this.monthlyTransactions = 0;
    }

    @Override
    public void processMonthlyFees() {
        withdraw(MONTHLY_FEE);
        monthlyTransactions = 0;
    }

    @Override
    protected boolean canWithdraw(BigDecimal amount) {
        return getBalance().subtract(amount).compareTo(OVERDRAFT_LIMIT) >= 0;
    }

    @Override
    public boolean transfer(Account destination, BigDecimal amount) {
        if (destination == this) {
            System.out.println("❌ Cannot transfer to the same account.");
            return false;
        }
        if (!canTransfer(amount)) {
            System.out.println("❌ Invalid transfer amount.");
            return false;
        }
        if (canWithdraw(amount)) {
            withdraw(amount);
            destination.deposit(amount);
            return true;
        }
        return false;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        super.withdraw(amount);
        monthlyTransactions++;
    }

    @Override
    public void deposit(BigDecimal amount) {
        super.deposit(amount);
        monthlyTransactions++;
    }

    public int getMonthlyTransactions() {
        return monthlyTransactions;
    }

    @Override
    public String toString() {
        return String.format("CheckingAccount[number=%s, balance=%.2f, transactions=%d]",
                getAccountNumber(), getBalance(), monthlyTransactions);
    }
}
