package com.banking.view;

import com.banking.model.AccountType;
import com.banking.service.AccountDAOImpl;
import com.banking.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Dashboard {
	private final Scanner scanner;
	private final AccountDAOImpl accountDAO;

	public Dashboard() {
		this.scanner = new Scanner(System.in);
		this.accountDAO = new AccountDAOImpl();
	}

	public void showMenu() {
		while (true) {
			System.out.println("\n==== Welcome to the Banking System ====");
			System.out.println("1. Admin Login");
			System.out.println("2. Customer Login");
			System.out.println("3. Exit");
			System.out.print("Choose an option: ");

			int choice = scanner.nextInt();

			switch (choice) {
				case 1:
					new Admin().showAdminMenu(); //
					break;
				case 2:
					int userId = handleCustomerLogin(); // ✅ Get logged-in user ID
					if (userId != -1) {
						showCustomerMenu(userId); // ✅ Pass userId (fixes error)
					} else {
						System.out.println("❌ Login failed. Returning to main menu...");
					}
					break;
				case 3:
					System.out.println("Exiting Banking System...");
					return;
				default:
					System.out.println("❌ Invalid option. Please try again.");
			}
		}
	}

	private int handleCustomerLogin() {
		System.out.print("Enter username: ");
		String username = scanner.next();

		System.out.print("Enter password: ");
		String password = scanner.next();

		int userId = accountDAO.authenticateUser(username, password); // ✅ Check credentials

		if (userId != -1) {
			System.out.println("✅ Login successful! Welcome, " + username);
			return userId; // ✅ Return user ID
		} else {
			System.out.println("❌ Invalid username or password.");
			return -1; // ❌ Login failed
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
				return rs.getInt("user_id"); // ✅ Return the user's ID
			}
		} catch (SQLException e) {
			System.err.println("❌ Error during login: " + e.getMessage());
		}
		return -1; // ❌ Login failed
	}


	private void showCustomerMenu(int userId) { // ✅ Accepts userId
		while (true) {
			System.out.println("\n==== Select Account Type ====");
			System.out.println("1. Savings Account");
			System.out.println("2. Checking Account");
			System.out.println("3. Back to Main Menu");
			System.out.print("Choose an option: ");

			int choice = scanner.nextInt();
			String accountType = "";

			switch (choice) {
				case 1:
					AccountType savings = AccountType.SAVINGS;
					break;
				case 2:
					AccountType checking = AccountType.CHECKING;
					break;
				case 3:
					return; // ✅ Return to main menu
				default:
					System.out.println("❌ Invalid option. Please try again.");
					continue;
			}

			handleTransactions(userId, accountType); // ✅ Pass userId and accountType
		}
	}


	private void handleTransactions(int userId, String accountType) {
		while (true) {
			System.out.println("\n==== " + accountType.toUpperCase() + " Account Options ====");
			System.out.println("1. Deposit");
			System.out.println("2. Withdraw");
			System.out.println("3. Transfer");
			System.out.println("4. Back");
			System.out.print("Choose an option: ");

			int choice = scanner.nextInt();

			switch (choice) {
				case 1:
					handleDeposit(userId, accountType);
					break;
				case 2:
					handleWithdrawal(userId, accountType);
					break;
				case 3:
					handleTransfer(userId, accountType);
					break;
				case 4:
					return; // ✅ Go back to account type selection
				default:
					System.out.println("❌ Invalid option. Please try again.");
			}
		}
	}


	private void handleDeposit(int userId, String accountType) {
		try {
			System.out.print("Enter your account number: ");
			String accountNumber = scanner.next();

			if (!accountDAO.isAccountOwnedByUser(accountNumber, userId, accountType)) {
				System.out.println("❌ You do not own this " + accountType + " account!");
				return;
			}

			System.out.print("Enter amount to deposit: ");
			BigDecimal amount = scanner.nextBigDecimal();

			boolean success = accountDAO.deposit(accountNumber, amount);

			if (success) {
				System.out.println("✅ Deposit successful!");
			} else {
				System.out.println("❌ Deposit failed.");
			}
		} catch (Exception e) {
			System.err.println("❌ Error processing deposit: " + e.getMessage());
		}
	}

	private void handleWithdrawal(int userId, String accountType) {
		try {
			System.out.print("Enter your account number: ");
			String accountNumber = scanner.next();

			if (!accountDAO.isAccountOwnedByUser(accountNumber, userId, accountType)) {
				System.out.println("❌ You do not own this " + accountType + " account!");
				return;
			}

			System.out.print("Enter amount to withdraw: ");
			BigDecimal amount = scanner.nextBigDecimal();

			boolean success = accountDAO.withdraw(accountNumber, amount);

			if (success) {
				System.out.println("✅ Withdrawal successful!");
			} else {
				System.out.println("❌ Insufficient funds or error occurred.");
			}
		} catch (Exception e) {
			System.err.println("❌ Error processing withdrawal: " + e.getMessage());
		}
	}

	private void handleTransfer(int userId, String accountType) {
		try {
			System.out.print("Enter your account number: ");
			String fromAccount = scanner.next();

			if (!accountDAO.isAccountOwnedByUser(fromAccount, userId, accountType)) {
				System.out.println("❌ You do not own this " + accountType + " account!");
				return;
			}

			System.out.print("Enter recipient's account number: ");
			String toAccount = scanner.next();

			System.out.print("Enter amount to transfer: ");
			BigDecimal amount = scanner.nextBigDecimal();

			boolean success = accountDAO.transfer(fromAccount, toAccount, amount);

			if (success) {
				System.out.println("✅ Transfer successful!");
			} else {
				System.out.println("❌ Transfer failed. Check account numbers or balance.");
			}
		} catch (Exception e) {
			System.err.println("❌ Error processing transfer: " + e.getMessage());
		}
	}

}
