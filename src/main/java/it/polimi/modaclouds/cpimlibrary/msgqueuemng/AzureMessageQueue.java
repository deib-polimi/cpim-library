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
package it.polimi.modaclouds.cpimlibrary.msgqueuemng;

import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import com.windowsazure.samples.queue.AzureQueueManagerFactory;
import com.windowsazure.samples.queue.AzureQueueMessageCollection;
import com.windowsazure.samples.queue.QueueOperationResponse;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.queue.*;
import it.polimi.modaclouds.cpimlibrary.*;
import it.polimi.kundera.client.azuretable.AzureTableClientFactory;


import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.configure.ClientProperties;
import com.impetus.kundera.configure.schema.api.SchemaManager;
import com.impetus.kundera.loader.ClientLoaderException;
import com.impetus.kundera.loader.GenericClientFactory;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import com.impetus.kundera.persistence.EntityReader;
import com.impetus.kundera.loader.GenericClientFactory;
import com.impetus.kundera.loader.*;
import it.polimi.modaclouds.cpimlibrary.mffactory.*;
import java.util.Map;
import java.util.HashMap;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.queue.*;


import java.lang.System;

class AzureMessageQueue implements CloudMessageQueue {

	private String queueName = null;
	private com.windowsazure.samples.queue.AzureQueueManager aqm = null;
	private String DELIMITER = "---";
	private CloudMetadata metadata;
	private String storageConnectionString;

	public AzureMessageQueue(String queueName, AzureQueueManagerFactory aqmf,
			QueueInfo queueInfo,CloudMetadata dati) {
		this.queueName = queueName;
		this.metadata = dati;

		this.storageConnectionString = buildConnectionString();

		creaCoda();


	}
	public String buildConnectionString() {
		String accountName = null;
		String accountKey = null;
		HashMap<String, String> externalProperties = metadata.getPersistenceInfo();
		if (externalProperties != null) {
			accountName = (String) externalProperties.get(PersistenceProperties.KUNDERA_USERNAME);
			accountKey = (String) externalProperties.get(PersistenceProperties.KUNDERA_PASSWORD);
		}
		if (accountName == null || accountKey == null) {
			throw new ClientLoaderException("Configuration error, check kundera.username kundera.password and in persistence.xml");
		}


		return "DefaultEndpointsProtocol=http;AccountName=" + accountName + ";AccountKey=" + accountKey;
	}
	public void creaCoda(){
		try
		{
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount =
					CloudStorageAccount.parse(storageConnectionString);

			// Create the queue client.
			CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

			// Retrieve a reference to a queue.
			CloudQueue queue = queueClient.getQueueReference("myqueue");

			// Create the queue if it doesn't already exist.
			queue.createIfNotExists();
			//System.out.println("ok ha creato la coda");
		}
		catch (Exception e)
		{
			// Output the stack trace.
			e.printStackTrace();
		}
	}
	@Override
	public void add(String msg) throws CloudMessageQueueException {
		aggiungiMessaggio(msg);
	}

	public void aggiungiMessaggio(String msg){
		try
		{
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount =
					CloudStorageAccount.parse(storageConnectionString);

			// Create the queue client.
			CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

			// Retrieve a reference to a queue.
			CloudQueue queue = queueClient.getQueueReference("myqueue");

			// Create the queue if it doesn't already exist.
			queue.createIfNotExists();

			// Create a message and add it to the queue.
			CloudQueueMessage message = new CloudQueueMessage(msg);
			queue.addMessage(message);
		}
		catch (Exception e)
		{
			// Output the stack trace.
			e.printStackTrace();
		}
	}
	@Override
	public String getQueueName() {
		return queueName;
	}

	@Override
	public void purge() {
		aqm.clearMessages(queueName);
	}

	@Override
	public CloudMessage getMessage() {

		return recuperaMessaggio();

	}

	public CloudMessage recuperaMessaggio(){

		try
		{
		CloudStorageAccount storageAccount =
				CloudStorageAccount.parse(storageConnectionString);

		// Create the queue client.
		CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

		// Retrieve a reference to a queue.
		CloudQueue queue = queueClient.getQueueReference("myqueue");

		// Create the queue if it doesn't already exist.
		queue.createIfNotExists();

// Peek at the next message
		CloudQueueMessage peekedMessage = queue.peekMessage();
		CloudMessage messaggio = new CloudMessage(peekedMessage.getMessageContentAsString());
			return messaggio;
// Display message.
		}
		catch (Exception e)
		{
			// Output the stack trace.
			e.printStackTrace();
			return null;
		}

	}

	public void eliminaMessaggio(){
		try
		{
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount =
					CloudStorageAccount.parse(storageConnectionString);

			// Create the queue client.
			CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

			// Retrieve a reference to a queue.
			CloudQueue queue = queueClient.getQueueReference("myqueue");

			// Retrieve the first visible message in the queue.
			CloudQueueMessage retrievedMessage = queue.retrieveMessage();

			if (retrievedMessage != null)
			{
				// Process the message in less than 30 seconds, and then delete the message.
				queue.deleteMessage(retrievedMessage);
			}
		}
		catch (Exception e)
		{
			// Output the stack trace.
			e.printStackTrace();
		}
	}

	@Override
	public void deleteMessage(String msgId) throws CloudMessageQueueException {


		eliminaMessaggio();
	}

	@Override
	public void deleteMessage(CloudMessage msg)
			throws CloudMessageQueueException {
		deleteMessage(msg.getId());
	}

}
