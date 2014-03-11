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

import java.util.ArrayList;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GlassfishBlobManager implements CloudBlobManager {
	
	String blobConnectionString=null;
	String user=null;
	String pwd=null;
	
	//il costruttore semplicemente registra la connessione da utilizzare per effettuare tutte le operazioni necessarie
	public GlassfishBlobManager(String blobConnectionString, String userName, String password) {
		this.blobConnectionString=blobConnectionString;
		this.user=userName;
		this.pwd=password;
	}

	@Override
	public void uploadBlob(byte[] file, String fileName) {
		
		Connection conn;
		try {
			
		conn = (Connection) DriverManager.getConnection(blobConnectionString,user,pwd);
		Blob b1 = conn.createBlob();
	    b1.setBytes(1, file);
	    PreparedStatement ps = conn.prepareStatement("insert into UserPicture(FileName,Picture) value (?,?)");
	    ps.setString(1, fileName);
	    ps.setBlob(2, b1);
	    ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void deleteBlob(String fileName) {
		
		Connection conn;
		try {
			conn = (Connection) DriverManager.getConnection(blobConnectionString,user,pwd);
			String sql="delete from UserPicture where FileName=?";
            PreparedStatement pst=conn.prepareStatement(sql);
            pst.setString(1, fileName);
            pst.executeUpdate(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	public CloudDownloadBlob downloadBlob(String fileName) {
		
		Connection conn;

		try {
			conn = (Connection) DriverManager.getConnection(blobConnectionString,user,pwd);
			String sql="select Picture from UserPicture up where up.FileName=?";
            PreparedStatement pst=conn.prepareStatement(sql);
            pst.setString(1, fileName);
            ResultSet rs=pst.executeQuery();
            if(rs.next()){
            	Blob b1 = rs.getBlob(1);
            	return new CloudDownloadBlob(fileName, b1.getBinaryStream(),null,b1.length(),b1.toString());
            }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public ArrayList<String> getAllBlobFileName() {
		Connection conn;

		try {
			conn = (Connection) DriverManager.getConnection(blobConnectionString,user,pwd);
			String sql="select FileName from UserPicture";
            PreparedStatement pst=conn.prepareStatement(sql);
            ResultSet rs=pst.executeQuery();
            ArrayList<String> toReturn=new ArrayList<String>();

            
            while(rs.next())
            {
            	toReturn.add(rs.getString(1));
            }
            
            return toReturn;
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
