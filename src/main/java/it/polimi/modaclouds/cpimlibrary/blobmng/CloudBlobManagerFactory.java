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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.NotSupportedVendorException;

/**
 * This abstract class allows to create a CloudBlobManager that contains the
 * method used to manage the blob(file) on the cloud, in an independet-platform
 * way. To instantiate this class must be called the
 * {@code getCloudBlobManagerFactory} static method.
 */
public abstract class CloudBlobManagerFactory {

	private static CloudBlobManagerFactory instance = null;

	/**
	 * This static method create a {@code CloudBlobManagerFactory} instance with
	 * the singleton tecnique. This method is platform-independent and uses the
	 * {@code CloudMetadata} object passed by paramenter to determine which type
	 * of cloud it was choosen.
	 * 
	 * @param metadata
	 * @return a CloudBlobManagerFactory instance
	 */
	public static CloudBlobManagerFactory getCloudBlobManagerFactory(
			CloudMetadata metadata) {
		if (instance == null) {
			if (metadata.getTypeCloud().equals("Azure")) {
				instance = new AzureBlobManagerFactory(metadata.getAccount(),
						metadata.getKey());
			} else if (metadata.getTypeCloud().equals("Google")) {
				instance = new GoogleBlobManagerFactory();
			} else if (metadata.getTypeCloud().equals("Amazon")) {
				instance = new AmazonBlobManagerFactory();
			}
			//include il caso di glassfish
			else if (metadata.getTypeCloud().equals("Glassfish")) {
				instance = new GlassfishBlobManagerFactory(metadata.getBlobConnectionString());
			} 
			else
				try {
					throw new NotSupportedVendorException("The vendor "
							+ metadata.getTypeCloud()
							+ " is not supported by the library");
				} catch (NotSupportedVendorException e) {
					e.printStackTrace();
				}
		}
		return instance;
	}

	/**
	 * If this method was called than the next call of
	 * {@code getCloudBlobManagerFactory} method returns a new instance.
	 */
	public void close() {
		instance = null;
	}

	/**
	 * Create a CloudBlobManager that contains the method to manage the Blob
	 * service.
	 * 
	 * @return a CloudBlobManager instance.
	 * @see CloudBlobManager
	 */
	public abstract CloudBlobManager createCloudBlobManager();

}
