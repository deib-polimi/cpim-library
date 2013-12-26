package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

import java.util.HashMap;

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;

public class GlassfishTaskQueueFactory extends CloudTaskQueueFactory {

	private HashMap<String, QueueInfo> info = null;

	
	public GlassfishTaskQueueFactory(CloudMetadata metadata) {
		this.info = metadata.getQueueMedatada();
	}

	@Override
	public CloudTaskQueue getQueue(String queueName) {
		QueueInfo queueInfo = info.get(queueName);
		if(queueInfo!=null){
			if (queueInfo.getMode().equals(ModeQueue.PUSH))
				return new GlassfishTaskQueue(queueName, queueInfo);
		}

		return null;
	}

}
