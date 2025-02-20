package com.banking;


import com.banking.util.DatabaseConnection;
import com.banking.util.DbConfig;
import com.banking.view.Dashboard;

import java.sql.SQLException;




public class Main {
	public static void main(String[] args) throws SQLException {
		Dashboard dashboard = new Dashboard();
		dashboard.showMenu();


	}
}
