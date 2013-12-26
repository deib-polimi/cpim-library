package it.polimi.modaclouds.cpimlibrary.blobmng;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GlassfishBlobManagerFactory extends CloudBlobManagerFactory {

	String blobConnectionString=null;
	
	//questo metodo crea nel database identificato dalla stringa di connessione passata una tabella che andrà a contenere i blob
	//istanzia quindi un nuovo glassfishblob manager passando la tringa di connessione in quanto necessaria per l inserimento dei blob
	public GlassfishBlobManagerFactory(String blobConnectionString) {
		
		Statement statement;
		Connection c=null;
		
		this.blobConnectionString=blobConnectionString;
		
		try {
			c = (Connection) DriverManager.getConnection(blobConnectionString,"deib-polimi","deib-polimi");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(c==null)
			System.out.println("CONNECTION TO DB FAILED");
		
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
		return new GlassfishBlobManager(this.blobConnectionString);
	}

}
