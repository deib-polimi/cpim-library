package it.polimi.modaclouds.cpimlibrary.sqlservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GlassfishSqlService extends CloudSqlService {
	private String connectionString = null;
	private String username=null;
	private String password=null;


	public GlassfishSqlService(String connectionString, String username, String password) {
		this.connectionString = connectionString;
		this.username=username;
		this.password=password;
	}

	@Override
	public Connection getConnection() {
		Connection c = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = (Connection) DriverManager.getConnection(connectionString,username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}

}
