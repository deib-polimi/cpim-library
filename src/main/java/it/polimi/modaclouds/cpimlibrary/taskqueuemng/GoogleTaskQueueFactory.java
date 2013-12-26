package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

/*
 * *****************************
 * cpim-library
 * *****************************
 * Copyright (C) 2013 deib-polimi
 * *****************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************
 */


import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import java.util.HashMap;

class GoogleTaskQueueFactory extends CloudTaskQueueFactory {

	private HashMap<String, QueueInfo> info = null;
	private String backend=null;

	public GoogleTaskQueueFactory(CloudMetadata metadata) {
		this.info = metadata.getQueueMedatada();
		this.backend=metadata.getBackend_name();
	}

	@Override
	public CloudTaskQueue getQueue(String queueName) {
		QueueInfo queueInfo = info.get(queueName);
		if (queueInfo.getMode().equals(ModeQueue.PUSH))
			return new GoogleTaskQueue(queueName,backend);
		return null;
	}

}
