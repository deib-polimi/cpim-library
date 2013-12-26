package it.polimi.modaclouds.cpimlibrary.msgqueuemng;


import java.util.HashMap;
import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;


public class GlassfishMessageQueueFactory extends CloudMessageQueueFactory {

	private HashMap<String, QueueInfo> info = null;

	public GlassfishMessageQueueFactory(CloudMetadata metadata) {
		//registra informazioni sulle code inserite per l'applicatione
		this.info = metadata.getQueueMedatada();

		
	}

	
	@Override
	public CloudMessageQueue getQueue(String queueName) {
		QueueInfo queueInfo = info.get(queueName);
		if (queueInfo.getMode().equals(ModeQueue.PULL))
			return new GlassfishMessageQueue(queueName);
		try {
			throw new CloudMessageQueueException("Wrong Mode...Please check your configurations in queue.xml");
		
		} catch (CloudMessageQueueException e) {
			e.printStackTrace();
			return null;
		}
	
	}

}
