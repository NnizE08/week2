package com.banking.model;

import java.math.BigDecimal;

public class SavingsAccount extends Account implements Transferable {
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00"); // Minimum required balance
    private final BigDecimal interestRate;

    public SavingsAccount(String accountNumber, BigDecimal balance) {
        super(accountNumber, balance, AccountType.SAVINGS); // ✅ Use AccountType Enum
        this.interestRate = new BigDecimal("0.025"); // ✅ 2.5% interest rate
    }

    public SavingsAccount(String accountNumber, BigDecimal balance, BigDecimal interestRate) {
        super(accountNumber, balance, AccountType.SAVINGS);
        this.interestRate = interestRate;
    }

    @Override
    public void processMonthlyFees() {
        BigDecimal interest = getBalance().multiply(interestRate);
        deposit(interest);
    }

    @Override
    protected boolean canWithdraw(BigDecimal amount) {
        return getBalance().subtract(amount).compareTo(MINIMUM_BALANCE) >= 0;
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

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount[number=%s, balance=%.2f, interestRate=%.2f%%]",
                getAccountNumber(), getBalance(), interestRate.multiply(new BigDecimal("100")));
    }
}
