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