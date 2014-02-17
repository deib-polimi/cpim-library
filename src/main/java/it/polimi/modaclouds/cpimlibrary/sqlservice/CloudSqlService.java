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
package it.polimi.modaclouds.cpimlibrary.sqlservice;

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.NotSupportedVendorException;

import java.sql.Connection;

/**
 * This abstract class allows to use the SQL service in an independent-platform
 * way. To instantiate this class you need to use the
 * {@code CloudSqlService.getCloudSqlService()} method.
 * 
 */
public abstract class CloudSqlService {

	/**
	 * This method instances a CloudSqlService object. It receives the
	 * <i>vendor</i> and the <i>connection_string</i> used to connect to the
	 * database service.
	 * 
	 * <p>
	 * If it passed the String like {@code "Azure"} or {@code "Google"} as
	 * vendor this method becomes cloud dependent, but if it passed the
	 * <i>vendor</i> contained in the CloudMetadata class calling the
	 * {@code getTypeCloud()} method, the method becomes platform-independet.
	 * </p>
	 * 
	 * @param vendor
	 * @param connection_string
	 * @return a CloudSqlService instance
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudSqlService getCloudSqlService(String vendor,
			String connection_string) {
		if (vendor.equals("Azure")) {
			return new AzureSqlService(connection_string);
		} else if (vendor.equals("Google")) {
			return new GoogleSqlService(connection_string);
		} else if (vendor.equals("Amazon")) {
			return new AmazonSqlService(connection_string);
		}
		//aggiunto caso glassfish
		else if (vendor.equals("Glassfish")) {
			try {
				throw new NotSupportedVendorException("The vendor " + vendor
						+ " is not supported by the library using thiS version of the method");
			} catch (NotSupportedVendorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}  else
			try {
				throw new NotSupportedVendorException("The vendor " + vendor
						+ " is not supported by the library");
			} catch (NotSupportedVendorException e) {
				e.printStackTrace();
				return null;
			}
	}

	
	/**
	 * This method instances a CloudSqlService class in an independent-platform way.
	 * It receives the CloudMetadata object to instantiate the specific service.
	 * 
	 * 
	 * @param metadata
	 * @return a CloudSqlService instance
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudSqlService getCloudSqlService(CloudMetadata metadata) {
		if (metadata.getTypeCloud().equals("Azure"))
			return new AzureSqlService(metadata.getConnectionString());
		else if (metadata.getTypeCloud().equals("Google")) {
			return new GoogleSqlService(metadata.getConnectionString());
		}
		//aggiunto caso glassfish
		else if (metadata.getTypeCloud().equals("Glassfish")) {
			return new GlassfishSqlService(metadata.getConnectionString(),metadata.getPersistenceInfo().get("account.name"), metadata.getPersistenceInfo().get("account.key"));
		} else
			try {
				throw new NotSupportedVendorException("The vendor "
						+ metadata.getTypeCloud()
						+ " is not supported by the library");
			} catch (NotSupportedVendorException e) {
				e.printStackTrace();
				return null;
			}
	}

	/**
	 * Connects to a SQL database on cloud platform and returns the
	 * {@link java.sql.Connection} interface.
	 * 
	 * @return a {@link java.sql.Connection} interface
	 * @see java.sql.Connection
	 */
	public abstract Connection getConnection();
}
