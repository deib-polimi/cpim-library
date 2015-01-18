/**
 * Copyright 2013 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.cpimlibrary.blobmng;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class GlassfishBlobManagerFactory extends CloudBlobManagerFactory {

	//String blobConnectionString=null;
	//String dbAccount=null;
	//String dbPwd=null;
    private DataSource dataSource;

	
	
	public GlassfishBlobManagerFactory(String blobDataSource) {
		
		Statement statement;
		Connection c=null;
		
		//this.blobConnectionString=blobConnectionString;
		//this.dbAccount=user;
		//this.dbPwd=password;
		
		try {
			Context ctx = new InitialContext();
			this.dataSource= (DataSource)ctx.lookup(blobDataSource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (dataSource != null) {
	            c=this.dataSource.getConnection();
	        } 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(c==null)
			System.out.println("CONNECTION TO BLOB_DB FAILED");
		
		String stm = "CREATE TABLE UserPicture (FileName VARCHAR(100) NOT NULL, Picture LONGBLOB NOT NULL, PRIMARY KEY(FileName))";

		try {
			
			statement = c.createStatement();
			statement.executeUpdate(stm);

			
			statement.close();
			c.close();
			
		} catch (Exception e) {
			
			System.out.println("ERROR CREATING CONNECTION TO DB");
			System.out.println("PRINTSTACKTRACE:"+e.getMessage());
		}
		
	}

	@Override
	public CloudBlobManager createCloudBlobManager() {
		try {
			return new GlassfishBlobManager(this.dataSource.getConnection());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
