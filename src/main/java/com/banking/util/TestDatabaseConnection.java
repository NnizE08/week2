package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseConnection {
	public static void main(String[] args) {
		try {
			String url = "jdbc:h2:~/bankingdb;MODE=MySQL;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
			String user = "sa";
			String password = "";

			Connection conn = DriverManager.getConnection(url, user, password);
			if (conn != null) {
				System.out.println("Connection successful!");
				conn.close();
			} else {
				System.out.println("Connection failed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
