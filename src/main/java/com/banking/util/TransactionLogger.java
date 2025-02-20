package com.banking.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class TransactionLogger {

	// ✅ Save transaction with type (Deposit, Withdrawal, Transfer)
	public static void saveTransaction(String accountNumber, String transactionType, BigDecimal amount, String referenceAccount) {
		String sql = "INSERT INTO transactions (account_number, transaction_type, amount, transaction_date, reference_account) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, accountNumber);
			stmt.setString(2, transactionType);
			stmt.setBigDecimal(3, amount);
			stmt.setObject(4, LocalDateTime.now());
			stmt.setString(5, referenceAccount); // Can be NULL for deposits/withdrawals

			stmt.executeUpdate();
			System.out.println("✅ Transaction saved successfully!");

		} catch (SQLException e) {
			System.err.println("❌ Could not save transaction: " + e.getMessage());
		}
	}

	// ✅ Show all transactions
	public static void showAllTransactions() {
		String sql = "SELECT account_number, transaction_type, amount, transaction_date, reference_account FROM transactions ORDER BY transaction_date DESC";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {

			if (!rs.isBeforeFirst()) {
				System.out.println("No transactions found.");
				return;
			}

			System.out.println("\n📜 Transaction History:");
			System.out.println("--------------------------------------------------");
			while (rs.next()) {
				System.out.printf("%s | %s | Account: %s | Amount: $%.2f | Reference: %s%n",
						rs.getTimestamp("transaction_date"),
						rs.getString("transaction_type"),
						rs.getString("account_number"),
						rs.getBigDecimal("amount"),
						rs.getString("reference_account") != null ? rs.getString("reference_account") : "N/A");
			}

		} catch (SQLException e) {
			System.err.println("❌ Could not retrieve transactions: " + e.getMessage());
		}
	}

	// ✅ Clear all transactions from the database
	public static void clearTransactions() {
		String sql = "DELETE FROM transactions";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			int rowsDeleted = stmt.executeUpdate();
			System.out.println("🗑️ Deleted " + rowsDeleted + " transactions.");

		} catch (SQLException e) {
			System.err.println("❌ Could not clear transactions: " + e.getMessage());
		}
	}
}
