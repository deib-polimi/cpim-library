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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AmazonMessageQueue implements CloudMessageQueue {

	private String queueName = null;
	private AmazonSQSClient sqs = null;
	private Map<String, String> receipts = new HashMap<String, String>();
	
	public AmazonMessageQueue(String queueName, AmazonSQSClient sqs) {
		this.queueName = queueName;
		this.sqs = sqs;
		CreateQueueRequest cqr = new CreateQueueRequest(queueName);
		sqs.createQueue(cqr);
	}
	
	@Override
	public void add(String msg) throws CloudMessageQueueException {
		GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
		GetQueueUrlResult getQueueUrlResult = sqs.getQueueUrl(getQueueUrlRequest);
		String queueUrl = getQueueUrlResult.getQueueUrl();
		sqs.sendMessage(new SendMessageRequest(queueUrl, msg));
	}

	@Override
	public String getQueueName() {
		return queueName;
	}

	@Override
	public void purge() {
		String queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
		Set<String> keys = receipts.keySet();
		for(String key: keys) {
			sqs.deleteMessage(new DeleteMessageRequest(queueUrl, receipts.get(key)));
		}
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for(Message m: messages) {
			sqs.deleteMessage(new DeleteMessageRequest(queueUrl, m.getReceiptHandle()));
		}
		receipts.clear();
	}

	@Override
	public CloudMessage getMessage() {
		String msg = null;
		String id = null;
		String queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for(Message m : messages) {
			msg = m.getBody();
			id = m.getMessageId();
			receipts.put(id, m.getReceiptHandle());
			break;
		}
		CloudMessage cm = new CloudMessage(msg);
		cm.setId(id);
		return cm;
	}

	@Override
	public void deleteMessage(CloudMessage msg) throws CloudMessageQueueException {
		this.deleteMessage(msg.id);
	}

	@Override
	public void deleteMessage(String msgId) throws CloudMessageQueueException {
		String queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
		sqs.deleteMessage(new DeleteMessageRequest(queueUrl, receipts.get(msgId)));
		receipts.remove(msgId);
	}


}
