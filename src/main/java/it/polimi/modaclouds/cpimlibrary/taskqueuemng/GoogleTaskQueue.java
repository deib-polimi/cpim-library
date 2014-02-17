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

import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

class GoogleTaskQueue implements CloudTaskQueue {

	private Queue queue = null;
	private String backend=null;
	Logger l=null;

	public GoogleTaskQueue(String queueName, String backend) {
		queue = QueueFactory.getQueue(queueName);
		this.backend=backend;
		
	}

	public void add(CloudTask t) throws CloudTaskQueueException {
		
		try{
			
			queue.add(buildTask(t));
		}
		catch(Exception e){
			throw new CloudTaskQueueException(e.getMessage());
		}
	}

	@Override
	public String getQueueName() {
		return queue.getQueueName();
	}

	@Override
	public void purge() throws CloudTaskQueueException {
		
		try{
			
			queue.purge();
		}
		catch(Exception e){
			throw new CloudTaskQueueException(e.getMessage());
		}
	}

	@Override
	public boolean delete(CloudTask t){
		try{
			
			TaskHandle th= new TaskHandle(buildTask(t),queue.getQueueName());
			return queue.deleteTask(th);
		}
		catch(Exception e){
			return false;
		}
		
	}

	private com.google.appengine.api.taskqueue.TaskOptions buildTask(
			CloudTask t) {
		l=Logger.getLogger("it.polimi.modaclouds.cpimlibrary");
		com.google.appengine.api.taskqueue.TaskOptions to = com.google.appengine.api.taskqueue.TaskOptions.Builder
				.withUrl(t.getServletUri().toString());
		to.taskName(t.getTaskName().replace("@", "AT").replace(".", "DOT"));
		for (String key : t.getParameters().keySet()) {
			to.param(key, t.getParameters().get(key));
		}
		to.method(Method.valueOf(t.getMethod()));
		
		if(backend!=null)
		{
			l.info("BACKEND:"+backend);
			to.header("Host",backend);
		}
		else
			l.info("BACKEND NULL");
		return to;
	}

}
