package it.polimi.modaclouds.cpimlibrary.mailservice;

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


import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.NotSupportedVendorException;

/**
 * This abstract class exposes the method to send a mail through the service on
 * the cloud, in a independent-platform way. To instantiate this class you need
 * to use the {@code CloudMailManager.getCloudMailManager()} method.
 */
public abstract class CloudMailManager {

	/**
	 * This method instances a CloudMailManager object in an
	 * independent-platform way, passing the CloudMetadata object.
	 * 
	 * @param metadata
	 * @return a CloudMailManager instance
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudMailManager getCloudMailManager(CloudMetadata metadata) {
		if (metadata.getTypeCloud().equals("Azure")) {
			return new AzureMailManager(metadata);
		} else if (metadata.getTypeCloud().equals("Google")) {
			return new GoogleMailManager(metadata);
		} else if (metadata.getTypeCloud().equals("Amazon")) {
			return new AmazonMailManager(metadata);
		}
		//aggiunto caso glassfish
		else if (metadata.getTypeCloud().equals("Glassfish")) {
			return new GlassfishMailManager(metadata);
		} else
			try {
				throw new NotSupportedVendorException("The vendor "
						+ metadata.getTypeCloud()
						+ " is not supported by the library");
			} catch (NotSupportedVendorException e) {
				e.printStackTrace();
			}
		return null;
	}

	/**
	 * Send a mail
	 * 
	 * @param msgToSend
	 *            - object that contains all the mail attribute, like the
	 *            recipient, the subject and the text message
	 */
	public abstract void sendMail(CloudMail msgToSend);

}
