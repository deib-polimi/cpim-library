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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;

public class GlassfishTaskQueueFactory extends CloudTaskQueueFactory {

	private HashMap<String, QueueInfo> info = null;
	private HashMap<String, GlassfishTaskQueue> map = null;

	
	public GlassfishTaskQueueFactory(CloudMetadata metadata) {
		this.info = metadata.getQueueMedatada();
	}

	@Override
	public CloudTaskQueue getQueue(String queueName) {
		
		QueueInfo queueInfo = info.get(queueName);
		
		if (map == null) {
			map = new HashMap<String, GlassfishTaskQueue>();
		}
		if (map.containsKey(queueName))
			return map.get(queueName);
		
		
		
		if(queueInfo!=null){
			if (queueInfo.getMode().equals(ModeQueue.PUSH)){
			GlassfishTaskQueue newqueue = new GlassfishTaskQueue(queueName, queueInfo);
			map.put(queueName, newqueue);
			return newqueue;
			}
		}
		return null;
	
	}
}