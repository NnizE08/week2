package com.banking.service;

import com.banking.model.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public abstract class AccountDAO {
	public abstract void createAccount(Account account);
	public abstract Optional<Account> getAccountByNumber(String accountNumber);
	public abstract void updateBalance(String accountNumber, BigDecimal newBalance);
	public abstract List<Account> getAllAccounts();
	public abstract void deleteAccount(String accountNumber);

	// ✅ Account Transactions
	public abstract boolean deposit(String accountNumber, BigDecimal amount);
	public abstract boolean withdraw(String accountNumber, BigDecimal amount);
	public abstract boolean transfer(String fromAccount, String toAccount, BigDecimal amount); // ✅ Add transfer method
}
