package com.banking.util;

import org.h2.tools.Server;

import java.sql.*;

public class DatabaseConnection {
	private static Connection connection;

	public static Connection connect() throws SQLException {
		if (connection == null || connection.isClosed()) {
			startH2Console();
			connection = DriverManager.getConnection(
					DbConfig.getUrl(),
					DbConfig.getUsername(),
					DbConfig.getPassword()
			);
			System.out.println("check!");
		}
		return connection;
	}

	public static void closeDatabase() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("✅ Database connection closed.");
			} catch (SQLException e) {
				System.err.println("❌ Error closing database connection: " + e.getMessage());
			}
		}
	}

	public static void closeResources(ResultSet rs, PreparedStatement ps1, PreparedStatement ps2, Connection conn) {
		try {
			if (rs != null) rs.close();
			if (ps1 != null) ps1.close();
			if (ps2 != null) ps2.close();
			if (conn != null && !conn.isClosed()) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private static void startH2Console() {
		try {
			// Explicitly bind to the right port and web server properties
			Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
			webServer.start();
			System.out.println("H2 Web Console started at http://localhost:8082");
		} catch (SQLException e) {
			System.err.println("Could not start H2 Web Console: " + e.getMessage());
		}
	}
}
