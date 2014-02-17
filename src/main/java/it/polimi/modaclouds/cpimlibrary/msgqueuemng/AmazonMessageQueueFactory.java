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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import java.io.IOException;
import java.util.HashMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class AmazonMessageQueueFactory extends CloudMessageQueueFactory {

	//serve salvarsi la map??? SI!!!
	private HashMap<String, AmazonMessageQueue> map = null;
	private HashMap<String, QueueInfo> info = null;
	
	private AmazonSQSClient sqs = null;
	
	//creo un SQS diverso per ogni factory??? quindi per ogni volta che lo richiamo nel provider???
	public AmazonMessageQueueFactory(CloudMetadata metadata) {
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(
			        getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sqs = new AmazonSQSClient(credentials);
		this.info = metadata.getQueueMedatada();
	}
	
	//se gia e stato creato un manager per una coda, lo restituisco; altrimenti lo creo
	@Override
	public CloudMessageQueue getQueue(String queueName) {
		queueName = queueName.toLowerCase();
		QueueInfo queueInfo= info.get(queueName);
		if (map == null) {
			map = new HashMap<String, AmazonMessageQueue>();
		}
		if (map.containsKey(queueName))
			return map.get(queueName);
		if(queueInfo.getMode().equals(ModeQueue.PULL)){
			AmazonMessageQueue newqueue = new AmazonMessageQueue(queueName, sqs);
			map.put(queueName, newqueue);
			return newqueue;
		}else{
			return null;
		}
	}

}
