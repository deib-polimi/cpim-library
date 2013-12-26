package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

import java.util.Queue;

import it.polimi.modaclouds.cpimlibrary.QueueInfo;

public class GlassfishTaskQueue implements CloudTaskQueue {

	private String name=null;
	private Queue<CloudTask> taskQueue=null;

	public GlassfishTaskQueue(String queueName, QueueInfo queueInfo) {
		this.name=queueName;
		new GlassfishTaskHandler(this, queueInfo);
		Queue q;
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

}
