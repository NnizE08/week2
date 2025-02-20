package com.banking.view;

import com.banking.model.Users;
import com.banking.service.UserDAOImpl;
import com.banking.util.DatabaseConnection;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Admin {
	private final Scanner scanner;
	private final UserDAOImpl userDAO;
	private Users loggedInAdmin;

	public Admin() {
		this.scanner = new Scanner(System.in);
		this.userDAO = new UserDAOImpl();
	}

	public void showAdminMenu() {
		System.out.println("\n==== Admin Login ====");
		if (!loginAdmin()) {
			System.out.println("❌ Unauthorized access. Exiting...");
			return;
		}

		while (true) {
			System.out.println("\n==== Admin Panel ====");
			System.out.println("1. View All Users");
			System.out.println("2. Create New User");
			System.out.println("3. Exit");
			System.out.print("Choose an option: ");

			int choice = scanner.nextInt();
			switch (choice) {
				case 1:
					viewAllUsers();
					break;
				case 2:
					createNewUser();
					break;
				case 3:
					System.out.println("Exiting");
					return;
				default:
					System.out.println("Invalid option. Please try again.");
			}
		}
	}

	private boolean loginAdmin() {
		System.out.print("Enter admin username: ");
		String username = scanner.next();

		System.out.print("Enter admin password: ");
		String password = scanner.next();

		int adminId = userDAO.authenticateUser(username, password);
		if (adminId > 0) {
			Optional<Users> adminUser = userDAO.getUserByUsername(username);
			adminUser.ifPresent(value -> this.loggedInAdmin = value);
			System.out.println("✅ Admin login successful!");
			return true;
		}
		return false;
	}

	private void viewAllUsers() {
		System.out.println("\n==== List of All Users ====");
		List<Users> users = userDAO.getAllUsers();
		if (users.isEmpty()) {
			System.out.println("No users found.");
			return;
		}
		for (Users user : users) {
			System.out.println(user);
		}
	}

	private void createNewUser() {
		System.out.print("Enter username: ");
		String username = scanner.next();

		System.out.print("Enter password: ");
		String password = scanner.next();

		System.out.print("Enter full name: ");
		scanner.nextLine(); // Consume newline
		String fullName = scanner.nextLine();

		System.out.print("Enter email: ");
		String email = scanner.next();

		System.out.print("Enter role (admin/customer): ");
		String role = scanner.next().toLowerCase();

		Users newUser = new Users(username, password, fullName, email, role);
		if (userDAO.createUser(newUser)) {
			System.out.println("✅ User created successfully!");
		} else {
			System.out.println("❌ Failed to create user.");
		}
	}
}
