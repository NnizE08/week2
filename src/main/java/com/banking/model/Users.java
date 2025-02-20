package com.banking.model;

public class Users {
	private String username;
	private String password;
	private String fullName;
	private String email;
	private String role;

	// Constructor
	public Users(String username, String password, String fullName, String email, String role) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
		this.role = role;
	}

	// Getters and setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Username: " + username + ", Full Name: " + fullName + ", Email: " + email + ", Role: " + role;
	}
}
