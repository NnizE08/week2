package com.banking.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
	private static final Properties properties = new Properties();

	static {
		try (InputStream input = DbConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
			if (input == null) {
				throw new RuntimeException("database.properties file not found in resources");
			}
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load database.properties", e);
		}
	}

	public static String getUrl() {
		return properties.getProperty("db.url", "jdbc:h2:~/bankingdb;MODE=MySQL;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1");
	}

	public static String getUsername() {
		return properties.getProperty("db.username", "sa"); // Default H2 username
	}

	public static String getPassword() {
		return properties.getProperty("db.password", ""); // Default H2 password
	}

	public static String getSettings() {
		return properties.getProperty("db.settings", ""); // Default to empty settings if not found
	}
}