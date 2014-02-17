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

import java.util.LinkedList;


import it.polimi.modaclouds.cpimlibrary.QueueInfo;

public class GlassfishTaskQueue implements CloudTaskQueue {

	private String name=null;
	private LinkedList<CloudTask> taskQueue=null;

	public GlassfishTaskQueue(String queueName, QueueInfo queueInfo) {
		this.name=queueName;
		this.taskQueue= new LinkedList<CloudTask>();
		

	}

	@Override
	public void add(CloudTask t) throws CloudTaskQueueException {
		this.taskQueue.add(t);
	}

	@Override
	public String getQueueName() {
		return this.name;
	}

	@Override
	public void purge() throws CloudTaskQueueException {
		this.taskQueue.clear();
	}

	@Override
	public boolean delete(CloudTask t) {
		return this.taskQueue.remove(t);
	}
	
	public CloudTask getNext(){
		if(!this.taskQueue.isEmpty()){
			CloudTask toReturn=this.taskQueue.getFirst();
			this.delete(toReturn);
			return toReturn;
		}
		return null;
	}
	

}
