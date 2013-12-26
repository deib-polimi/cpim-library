package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

import it.polimi.modaclouds.cpimlibrary.QueueInfo;

public class GlassfishTaskHandler {

	private GlassfishTaskQueue queue=null;
	private QueueInfo info=null;
	
	public GlassfishTaskHandler(GlassfishTaskQueue glassfishTaskQueue,
			QueueInfo queueInfo) {
		this.queue=glassfishTaskQueue;
		this.info=queueInfo;
		this.execute();
		
	}
	
	private void execute()
	{
		
	}

}
