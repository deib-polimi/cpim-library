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
import java.util.logging.Logger;

import com.google.appengine.api.rdbms.AppEngineDriver;


/**
 * This class allows to use the SQL service on the Google App Engine platform,
 * through the {@link java.sql.Connection} interface.
 * 
 */
class GoogleSqlService extends CloudSqlService {
	private String connectionString = null;

	public GoogleSqlService(String connectionString) {
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
		// "jdbc:google:rdbms://localhost/micdb", "root", "mic"
		Connection c = null;
		try {
			DriverManager.registerDriver(new AppEngineDriver());
			c =  DriverManager.getConnection(connectionString);
		} catch (Exception e) {
			Logger l=Logger.getLogger("it.polimi.modaclouds.cpimlibrary");
			l.info("ERROR CREATING CONNECTION TO DB:"+connectionString);
			l.info("PRINTSTACKTRACE:"+e.getMessage());
			
		}
		return c;
	}

}
