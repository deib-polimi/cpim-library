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


import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import com.windowsazure.samples.queue.AzureQueueManagerFactory;
import com.windowsazure.samples.queue.AzureQueueMessageCollection;
import com.windowsazure.samples.queue.QueueOperationResponse;

class AzureMessageQueue implements CloudMessageQueue {

	private String queueName = null;
	private com.windowsazure.samples.queue.AzureQueueManager aqm = null;
	private String DELIMITER = "---";

	public AzureMessageQueue(String queueName, AzureQueueManagerFactory aqmf,
			QueueInfo queueInfo) {
		this.queueName = queueName;
		aqm = aqmf.createQueueManager();
		aqm.createQueue(queueName);
	}

	@Override
	public void add(String msg) throws CloudMessageQueueException {
		Integer timeToLiveInterval = null;
		QueueOperationResponse resp = aqm.putMessage(queueName, msg,
				timeToLiveInterval);
		if (!resp.getHttpStatusCode().isSuccess()) {

			throw new CloudMessageQueueException("Adding message:---" + msg
					+ "--- in the queue failed....");
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
		String text = null;
		String id = null;
		String pop = null;
		CloudMessage cmsg = null;
		AzureQueueMessageCollection msgs = (AzureQueueMessageCollection) aqm
				.getMessages(queueName, 1, null);
		if (msgs.getMessages().size() == 0) {
			return null;
		}
		for (com.windowsazure.samples.queue.AzureQueueMessage msg : msgs) {
			text = msg.getMessageText();
			id = msg.getMessageId();
			pop = msg.getPopReceipt();
		}
		cmsg = new CloudMessage(text);
		cmsg.setId(id + DELIMITER + pop);
		return cmsg;
	}

	@Override
	public void deleteMessage(String msgId) throws CloudMessageQueueException {

		String[] ids = msgId.split(DELIMITER);
		;
		String id = ids[0];
		String pop = ids[1];
		QueueOperationResponse resp = aqm.deleteMessage(queueName, id, pop);
		if (!resp.getHttpStatusCode().isSuccess()) {
			throw new CloudMessageQueueException(
					"ERROR in deleting msg with id!\n" + "INFO ABOUT ERROR:\n"
							+ "errorcode:" + resp.getErrorCode()
							+ "\nhttpresponsecode" + resp.getHttpStatusCode());
		}

	}

	@Override
	public void deleteMessage(CloudMessage msg)
			throws CloudMessageQueueException {
		deleteMessage(msg.getId());
	}

}
