package com.banking.service;

import com.banking.model.Users;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
	boolean createUser(Users user); // Use Users object

	List<Users> getAllUsers();

	Optional<Users> getUserByUsername(String username);

	int authenticateUser(String username, String password);
}
