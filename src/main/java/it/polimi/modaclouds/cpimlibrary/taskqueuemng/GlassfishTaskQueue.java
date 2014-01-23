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
