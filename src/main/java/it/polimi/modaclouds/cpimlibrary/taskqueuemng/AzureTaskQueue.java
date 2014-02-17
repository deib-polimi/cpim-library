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

import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import com.windowsazure.samples.queue.AzureQueueMessageCollection;
import com.windowsazure.samples.queue.QueueOperationResponse;

class AzureTaskQueue implements CloudTaskQueue {

	
	private String queueName = null;
	private com.windowsazure.samples.queue.AzureQueueManager aqm = null;
	QueueInfo queueInfo=null;
	
	
	public AzureTaskQueue(String queueName,
		com.windowsazure.samples.queue.AzureQueueManagerFactory aqmf, QueueInfo queueInfo) {
		this.queueName = queueName;
		aqm = aqmf.createQueueManager();
		//The Internal Worker'queue is responsible to create it
		//aqm.createQueue(queueName);
		this.queueInfo=queueInfo;
	
	
		
	}



	@Override
	public void add(CloudTask t) throws CloudTaskQueueException {
		Integer timeToLiveInterval = null;
		QueueOperationResponse resp= aqm.putMessage(queueName, buildMessage(t), timeToLiveInterval);
		if (!resp.getHttpStatusCode().isSuccess())
			throw new CloudTaskQueueException(resp.getException().getMessage());
	}

//	public AzureTaskQueueMessage getMessage() {
//		String text = null;
//		String id = null;
//		String pop = null;
//		AzureQueueMessageCollection msgs = (AzureQueueMessageCollection) aqm
//				.getMessages(queueName, 1, null);
//		if (msgs.getMessages().size() == 0)
//			return null;
//		for (com.windowsazure.samples.queue.AzureQueueMessage msg : msgs) {
//			text = msg.getMessageText();
//			id = msg.getMessageId();
//			pop = msg.getPopReceipt();
//		}
//		return new AzureTaskQueueMessage(id, text, pop);
//	}

	//For external deletion
	public boolean delete(CloudTask t) {
		String message = buildMessage(t);
		String text=null;
		String pop=null;
		String id=null;
		boolean trovato=false;
		int count = aqm.getQueueMetadata(queueName).getApproximateMessageCount();
		AzureQueueMessageCollection msgs = (AzureQueueMessageCollection) aqm
		.getMessages(queueName, count, null);
		if (msgs.getMessages().size() != 0)
			for (com.windowsazure.samples.queue.AzureQueueMessage msg : msgs) {
				text = msg.getMessageText();
				if(text.compareTo(message)==0){
					pop=msg.getPopReceipt();
					id=msg.getMessageId();
					trovato=true;
					break;
				}
		}
		QueueOperationResponse resp=null;
		if(trovato)
		{
			resp=aqm.deleteMessage(queueName, id, pop);
			if(!resp.getHttpStatusCode().isSuccess())
				return false;
			else
				return true;
		}
		else
			return false;
		
	}

	@Override
	public String getQueueName() {
		return queueName;
	}

	@Override
	public void purge() {
		aqm.clearMessages(queueName);		
	}

	
	private String buildMessage(CloudTask task){
		String parameters = "";
		for (String key : task.getParameters().keySet()) {
			parameters = parameters + "<PARAMETER>" + "<KEY>" + key + "</KEY>"
					+ "<VALUE>" + task.getParameters().get(key) + "</VALUE>"
					+ "</PARAMETER>";
		}
		//<URL>" + task.getHostUri()+":"+task.getPort()+task.getContextPath()+"</URL>
		String msg = "<MSG><SERVLET>"+task.getServletUri() + "</SERVLET><METHOD>"
				+ task.getMethod() + "</METHOD>" + parameters + "</MSG>";
		return msg;
	}


	/*
	private HttpResult buildResult(HttpMethod method,
			BufferedReader reader) throws Exception {

		// Extract the Http status from the response line.
		String statusLine = reader.readLine();
		String[] parts = statusLine.split(" ");
		int status = Integer.parseInt(parts[1].trim());
		HttpStatusCode statusCode = HttpStatusCode.fromInt(status);

		// Extract the headers.
		HttpHeader headers = new HttpHeader();
		String headerLine = reader.readLine().trim();
		while (headerLine.length() > 0) {
			parts = headerLine.split(":");
			headers.put(parts[0].trim(), parts[1].trim());
			headerLine = reader.readLine();
		}

		// Extract the body.
		StringBuffer sb = new StringBuffer();
		String bodyLine = reader.readLine();
		while (bodyLine != null) {
			sb.append(bodyLine);
			bodyLine = reader.readLine();
		}
		String body = sb.toString();

		return new HttpResult(method, statusCode, headers, body);
	}

	*/
	
}
