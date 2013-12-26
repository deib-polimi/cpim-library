package it.polimi.modaclouds.cpimlibrary.sqlservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GlassfishSqlService extends CloudSqlService {
	private String connectionString = null;


	public GlassfishSqlService(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public Connection getConnection() {
		Connection c = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = (Connection) DriverManager.getConnection(connectionString,"deib-polimi","deib-polimi");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}

}
