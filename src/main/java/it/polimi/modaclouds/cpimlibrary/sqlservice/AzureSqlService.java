package it.polimi.modaclouds.cpimlibrary.sqlservice;

/*
 * *****************************
 * cpim-library
 * *****************************
 * Copyright (C) 2013 deib-polimi
 * *****************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class allows to use the SQL service on the Windows Azure platform,
 * through the {@link java.sql.Connection} interface.
 * 
 */
class AzureSqlService extends CloudSqlService {

	private String connectionString = null;

	public AzureSqlService(String connectionString) {
		this.connectionString = connectionString;
	}

	/**
	 * Connects to a SQL database on the Windows Azure platform and returns the
	 * {@link java.sql.Connection} interface.
	 * 
	 * @param connectionString
	 * @return a {@link java.sql.Connection} interface
	 * @see java.sql.Connection
	 */
	public Connection getConnection() {
		// String connectionstr2 =
		// "jdbc:sqlserver://127.0.0.1:1433;databaseName=micdb;user=root;password=mic";
		Connection c = null;
		try {

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			c = (Connection) DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}
}
