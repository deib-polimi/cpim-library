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

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;


class GoogleMessageQueue implements CloudMessageQueue {
    private Queue q=null;
	public GoogleMessageQueue(String queueName) {
		q=QueueFactory.getQueue(queueName);
	}

	@Override
	public void add(String msg) throws CloudMessageQueueException {
		String taskName=String.valueOf(msg.hashCode());
		try
		{
		q.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(msg).taskName(taskName));
		}
		catch(Exception e)
		{
			throw new CloudMessageQueueException("Error in adding a message....");

		}
	}

	@Override
	public String getQueueName() {
		return q.getQueueName();
	}

	@Override
	public void purge() {
       q.purge();
	}

	@Override
	public CloudMessage getMessage() {
	    String msg=null;
		List<TaskHandle> tasks = q.leaseTasks(3600, TimeUnit.SECONDS, 1);
		 
		    if (tasks.size() == 0) {
		      
		    }
		    else
		    {
		     try {
				msg=new String(tasks.get(0).getPayload(),"UTF-8");
				return new CloudMessage(msg);
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		    return null;
		    
		
	}

	@Override
	public void deleteMessage(String msgId) throws CloudMessageQueueException {
		try
		{
		q.deleteTask(msgId);
		}
		catch(Exception e)
		{
			throw new CloudMessageQueueException("Error in deletion message with id:"+msgId);

		}
		
	}
	
	@Override
	public void deleteMessage(CloudMessage msg) throws CloudMessageQueueException {
		try
		{
		 q.deleteTask(msg.getId());
		}
		 catch(Exception e)
			{
				throw new CloudMessageQueueException("Error in deletion message with id:"+msg.getId());

			}
	}	

}
