package com.banking.service;

import com.banking.model.Users;
import com.banking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
	@Override
	public boolean createUser(Users user) {
		String sql = "INSERT INTO users (username, password, full_name, email, user_role) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFullName());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getRole());

			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.err.println("❌ Error creating user: " + e.getMessage());
			return false;
		}
	}

	@Override
	public List<Users> getAllUsers() {
		List<Users> users = new ArrayList<>();
		String sql = "SELECT * FROM users";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Users user = new Users(
						rs.getString("username"),
						rs.getString("password"),
						rs.getString("full_name"),
						rs.getString("email"),
						rs.getString("role")
				);
				users.add(user);
			}
		} catch (SQLException e) {
			System.err.println("❌ Error fetching users: " + e.getMessage());
		}
		return users;
	}

	@Override
	public Optional<Users> getUserByUsername(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		try (Connection conn = DatabaseConnection.connect();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Users user = new Users(
						rs.getString("username"),
						rs.getString("password"),
						rs.getString("full_name"),
						rs.getString("email"),
						rs.getString("role")
				);
				return Optional.of(user);
			}
		} catch (SQLException e) {
			System.err.println("❌ Error fetching user: " + e.getMessage());
		}
		return Optional.empty();
	}

	@Override
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
			System.err.println("❌ Error authenticating user: " + e.getMessage());
		}
		return -1; // User not found
	}
}
