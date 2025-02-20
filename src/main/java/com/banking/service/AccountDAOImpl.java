package com.banking.service;


import com.banking.exception.AccountNotFoundException;
import com.banking.model.Account;
import com.banking.model.SavingsAccount;
import com.banking.model.CheckingAccount;
import com.banking.util.DatabaseConnection;
import com.banking.util.TransactionLogger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAOImpl extends AccountDAO {

	@Override
	public void createAccount(Account account) {
		String sql = "INSERT INTO accounts (account_number, balance, account_type) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, account.getAccountNumber());
			stmt.setBigDecimal(2, account.getBalance());
			stmt.setString(3, getAccountType(account));
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error creating account", e);
		}
	}

	public Account createAccount(String accountNumber, BigDecimal balance, String accountType) {
		String sql = "INSERT INTO accounts (account_number, balance, account_type) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, accountNumber);
			stmt.setBigDecimal(2, balance);
			stmt.setString(3, accountType);
			stmt.executeUpdate();

			return createAccountInstance(accountNumber, balance, accountType);
		} catch (SQLException e) {
			throw new RuntimeException("Error creating account", e);
		}
	}

	@Override
	public Optional<Account> getAccountByNumber(String accountNumber) {
		String sql = "SELECT * FROM accounts WHERE account_number = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, accountNumber);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String accountType = rs.getString("account_type").toLowerCase();
				BigDecimal balance = rs.getBigDecimal("balance");

				Account account;
				if ("savings".equals(accountType)) {
					account = new SavingsAccount(accountNumber, balance);
				} else if ("checking".equals(accountType)) {
					account = new CheckingAccount(accountNumber, balance);
				} else {
					throw new RuntimeException("❌ Unknown account type: " + accountType);
				}

				return Optional.of(account);
			} else {
				throw new AccountNotFoundException("❌ Account number " + accountNumber + " not found.");
			}
		} catch (SQLException e) {
			throw new RuntimeException("❌ Error retrieving account: " + e.getMessage());
		}
	}



	@Override
	public void updateBalance(String accountNumber, BigDecimal newBalance) {
		String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBigDecimal(1, newBalance);
			stmt.setString(2, accountNumber);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error updating balance", e);
		}
	}

	@Override
	public List<Account> getAllAccounts() {
		List<Account> accounts = new ArrayList<>();
		String sql = "SELECT * FROM accounts";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				accounts.add(createAccountInstance(
						rs.getString("account_number"),
						rs.getBigDecimal("balance"),
						rs.getString("account_type")
				));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving accounts", e);
		}
		return accounts;
	}

	@Override
	public void deleteAccount(String accountNumber) {
		String sql = "DELETE FROM accounts WHERE account_number = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, accountNumber);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error deleting account", e);
		}
	}

	private String getAccountType(Account account) {
		if (account instanceof SavingsAccount) {
			return "savings";
		} else if (account instanceof CheckingAccount) {
			return "checking";
		} else {
			throw new IllegalArgumentException("Unknown account type");
		}
	}

	private Account createAccountInstance(String accountNumber, BigDecimal balance, String accountType) {
		switch (accountType.toLowerCase()) {
			case "savings":
				return new SavingsAccount(accountNumber, balance);
			case "checking":
				return new CheckingAccount(accountNumber, balance);
			default:
				throw new IllegalArgumentException("Unknown account type: " + accountType);
		}
	}

	public boolean createUser(String username, String password, String fullName, String email) {
		String sql = "INSERT INTO users (username, password, full_name, email) VALUES (?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password); // ⚠ Hash passwords in a real app
			stmt.setString(3, fullName);
			stmt.setString(4, email);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("❌ Error creating user: " + e.getMessage());
			return false;
		}
	}

	public int authenticateUser(String username, String password) {
		String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("user_id");
			}
		} catch (SQLException e) {
			System.err.println("❌ Error during login: " + e.getMessage());
		}
		return -1;
	}

	public List<Account> getUserAccounts(int userId) {
		List<Account> accounts = new ArrayList<>();
		String sql = "SELECT * FROM accounts WHERE user_id = ?";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				accounts.add(createAccountInstance(
						rs.getString("account_number"),
						rs.getBigDecimal("balance"),
						rs.getString("account_type")
				));
			}
		} catch (SQLException e) {
			System.err.println("❌ Error retrieving user accounts: " + e.getMessage());
		}
		return accounts;
	}

	public boolean createAccount(String accountNumber, BigDecimal balance, String accountType, int userId) {
		String sql = "INSERT INTO accounts (account_number, balance, account_type, user_id) VALUES (?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, accountNumber);
			stmt.setBigDecimal(2, balance);
			stmt.setString(3, accountType);
			stmt.setInt(4, userId);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("❌ Error creating account: " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean deposit(String accountNumber, BigDecimal amount) {
		Optional<Account> account = getAccountByNumber(accountNumber);
		if (!account.isPresent()) {
			throw new AccountNotFoundException("❌ Account not found: " + accountNumber);
		}

		String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBigDecimal(1, amount);
			stmt.setString(2, accountNumber);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				TransactionLogger.saveTransaction(accountNumber, "DEPOSIT", amount, null);
				return true;
			}
			return false;
		} catch (SQLException e) {
			throw new RuntimeException("❌ Error processing deposit: " + e.getMessage());
		}
	}



	@Override
	public boolean withdraw(String accountNumber, BigDecimal amount) {
		Optional<Account> account = getAccountByNumber(accountNumber);
		if (!account.isPresent()) {
			throw new AccountNotFoundException("❌ Account not found: " + accountNumber);
		}

		String sql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBigDecimal(1, amount);
			stmt.setString(2, accountNumber);
			stmt.setBigDecimal(3, amount);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				TransactionLogger.saveTransaction(accountNumber, "WITHDRAWAL", amount, null);
				return true;
			}
			return false;
		} catch (SQLException e) {
			throw new RuntimeException("❌ Error processing withdrawal: " + e.getMessage());
		}
	}



	@Override
	public boolean transfer(String fromAccount, String toAccount, BigDecimal amount) {
		Connection conn = null;
		PreparedStatement withdrawStmt = null;
		PreparedStatement depositStmt = null;

		try {
			conn = DatabaseConnection.connect();
			conn.setAutoCommit(false); // ✅ Begin transaction

			Optional<Account> sender = getAccountByNumber(fromAccount);
			Optional<Account> recipient = getAccountByNumber(toAccount);

			if (!sender.isPresent()) {
				throw new AccountNotFoundException("❌ Sender account not found: " + fromAccount);
			}

			if (!recipient.isPresent()) {
				throw new AccountNotFoundException("❌ Recipient account not found: " + toAccount);
			}

			// ✅ Withdraw from sender
			withdrawStmt = conn.prepareStatement(
					"UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?");
			withdrawStmt.setBigDecimal(1, amount);
			withdrawStmt.setString(2, fromAccount);
			withdrawStmt.setBigDecimal(3, amount);
			int withdrawSuccess = withdrawStmt.executeUpdate();
			if (withdrawSuccess == 0) {
				conn.rollback();
				System.err.println("❌ Transfer failed: Insufficient funds.");
				return false;
			}

			// ✅ Deposit to receiver
			depositStmt = conn.prepareStatement(
					"UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
			depositStmt.setBigDecimal(1, amount);
			depositStmt.setString(2, toAccount);
			int depositSuccess = depositStmt.executeUpdate();
			if (depositSuccess == 0) {
				conn.rollback();
				System.err.println("❌ Transfer failed: Recipient account not found.");
				return false;
			}

			// ✅ Log transaction
			TransactionLogger.saveTransaction(fromAccount, "TRANSFER", amount, toAccount);
			TransactionLogger.saveTransaction(toAccount, "TRANSFER", amount, fromAccount);

			conn.commit(); // ✅ Commit transaction
			System.out.println("✅ Transfer successful!");
			return true;
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					System.err.println("❌ Error rolling back transfer: " + rollbackEx.getMessage());
				}
			}
			throw new RuntimeException("❌ Error processing transfer: " + e.getMessage());
		} finally {
			DatabaseConnection.closeResources(null, withdrawStmt, depositStmt, conn);
		}
	}
	public boolean isAccountOwnedByUser(String accountNumber, int userId, String accountType) {
		String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ? AND user_id = ? AND account_type = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, accountNumber);
			stmt.setInt(2, userId);
			stmt.setString(3, accountType);
			ResultSet rs = stmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0; // ✅ Returns true if account belongs to user
		} catch (SQLException e) {
			System.err.println("❌ Error checking account ownership: " + e.getMessage());
		}
		return false;
	}

}
