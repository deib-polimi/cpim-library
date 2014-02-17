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
package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

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

public class AmazonTaskQueue implements CloudTaskQueue {

	private String queueName = null;
	private AmazonSQSClient sqs = null;
	private Map<String, String> cloud_task_ids = new HashMap<String, String>();
	private Map<String, String> receipts = new HashMap<String, String>();
	
	public AmazonTaskQueue(String queueName, AmazonSQSClient sqs) {
		this.queueName = queueName;
		this.sqs = sqs;
		CreateQueueRequest cqr = new CreateQueueRequest(queueName);
		sqs.createQueue(cqr);		
		
		//new InternalWorker(queueName,queueInfo).start();
	}

	@Override
	public void add(CloudTask t) throws CloudTaskQueueException {
		GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
		GetQueueUrlResult getQueueUrlResult = sqs.getQueueUrl(getQueueUrlRequest);
		String queueUrl = getQueueUrlResult.getQueueUrl();
		String id =  sqs.sendMessage(new SendMessageRequest(queueUrl, buildMessage(t))).getMessageId();
		cloud_task_ids.put(t.getTaskName(), id);
	}

	@Override
	public String getQueueName() {
		return this.queueName;
	}

	@Override
	public void purge() throws CloudTaskQueueException {
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
		cloud_task_ids.clear();
	}

	@Override
	public boolean delete(CloudTask t) {	
		String queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
		String id = cloud_task_ids.get(t.getTaskName());
		if(receipts.get(id)!=null) {
			sqs.deleteMessage(new DeleteMessageRequest(queueUrl, receipts.get(id)));
			receipts.remove(id);
			cloud_task_ids.remove(t.getTaskName());
			return true;
		}
		return false;
	}
	
	public boolean deleteMessage(AmazonTaskQueueMessage atqm) {
		String queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
		if(atqm.getPopReceipt()!=null) {
			sqs.deleteMessage(new DeleteMessageRequest(queueUrl, atqm.getPopReceipt()));
			receipts.remove(atqm.getMessageId());
			return true;
		}
		return false;
	}
	
	private String buildMessage(CloudTask task){
		String parameters = "";
		for (String key : task.getParameters().keySet()) {
			parameters = parameters + "<PARAMETER>" + "<KEY>" + key + "</KEY>"
					+ "<VALUE>" + task.getParameters().get(key) + "</VALUE>"
					+ "</PARAMETER>";
		}
		//aggiunto il tag URL all'interno di MSG
		//String msg = "<URL>" + task.getHostUri()+":"+task.getPort()+task.getContextPath()+"</URL>";
		//msg += "<MSG><SERVLET>"+task.getServletUri() + "</SERVLET><METHOD>"
		//	+ task.getMethod() + "</METHOD>" + parameters + "</MSG>";
		//String msg = "<MSG><URL>" + task.getHostUri() + "</URL>";
		String msg = "<MSG><SERVLET>"+task.getServletUri() + "</SERVLET><METHOD>"
				+ task.getMethod() + "</METHOD>" + parameters + "</MSG>";
		return msg;
	}
	
	public AmazonTaskQueueMessage getMessage() {
		String text = null;
		String id = null;
		String pop = null;
		String queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		if(messages.size()==0)
			return null;
		for(Message m : messages) {
			text = m.getBody();
			id = m.getMessageId();
			pop = m.getReceiptHandle();
			receipts.put(id, pop);
			break;
		}
		return new AmazonTaskQueueMessage(id, text, pop);
	}
	
	
}
