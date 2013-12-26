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


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * This class allows to use the SQL service on the Google App Engine platform,
 * through the {@link java.sql.Connection} interface.
 * 
 */
public class AmazonSqlService extends CloudSqlService {
	
	private String connectionString = null;
	private String user = null;
	private String password = null;

	public AmazonSqlService(String connectionString) {
		this.connectionString = connectionString;
	}
	
	/**
	 * Connects to a SQL database on the Google App Engine platform and returns
	 * the {@link java.sql.Connection} interface.
	 * 
	 * @param connectionString
	 * @return a {@link java.sql.Connection} interface
	 * @see java.sql.Connection
	 */
	public Connection getConnection() {

		
		if(this.user == null) {
			try {
			Properties endpoints = new Properties();
			if(this.getClass().getResourceAsStream("/endpoints.properties")!=null) {
				endpoints.load(this.getClass().getResourceAsStream("/endpoints.properties"));
				if (endpoints.getProperty("RDS-user")!=null)
					this.user = endpoints.getProperty("RDS-user");
			}
			if(this.user == null)
				this.user = "awsuser";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(this.password == null) {
			try {
			Properties endpoints = new Properties();
			if(this.getClass().getResourceAsStream("/endpoints.properties")!=null) {
				endpoints.load(this.getClass().getResourceAsStream("/endpoints.properties"));
				if (endpoints.getProperty("RDS-password")!=null)
					this.password = endpoints.getProperty("RDS-password");
			}
			if(this.user == null)
				this.user = "mypassword";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Connection c = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println(connectionString);
			c = DriverManager.getConnection(connectionString, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}

}
