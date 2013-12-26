package it.polimi.modaclouds.cpimlibrary.msgqueuemng;

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
 * This abstract class allows to create a MessageQueue on the cloud, in an
 * independent-platform way. To instantiate this class you need to use the
 * {@code CloudMessageQueueFactory.getCloudMessageQueueFactory()} method.
 * 
 * <p>
 * The MessageQueue is create only if it is configured in the <u>queue.xml</u>
 * file, and the mode of the relative queue is PULL.
 * </p>
 */
public abstract class CloudMessageQueueFactory {

	private static CloudMessageQueueFactory instance = null;

	/**
	 * This method instances a CloudMessageQueueFactory object with the
	 * singleton technique, in an independent-platform way, passing the
	 * CloudMetadata object.
	 * 
	 * @param metadata
	 * @return a CloudMessageQueueFactory instance
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudMessageQueueFactory getCloudMessageQueueFactory(
			CloudMetadata metadata) {
		if (instance == null) {
			if (metadata.getTypeCloud().equals("Azure")) {
				instance = new AzureMessageQueueFactory(metadata);
			} else if (metadata.getTypeCloud().equals("Google")) {
				instance = new GoogleMessageQueueFactory(metadata);
			} else if (metadata.getTypeCloud().equals("Amazon")) {
				instance = new AmazonMessageQueueFactory(metadata);
			} else if (metadata.getTypeCloud().equals("Glassfish")) {
				instance = new GlassfishMessageQueueFactory(metadata);
			} else
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
	 * {@code getCloudMessageQueueFactory} method returns a new instance.
	 */
	public void close() {
		instance = null;
	}

	/**
	 * Returns a queue with the given name.
	 * 
	 * @param queueName
	 * @return a CloudMessageQueue
	 * @see CloudMessageQueue
	 */
	public abstract CloudMessageQueue getQueue(String queueName);
}
