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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.xml.stream.XMLStreamException;

//import jpa4azure.impl.AzureProvider;

class AzureMessageQueueFactory extends
		CloudMessageQueueFactory {

//	private AzureProvider provider = null;
	private HashMap<String, AzureMessageQueue> map = null;
	private HashMap<String, QueueInfo> info = null;

	private com.windowsazure.samples.queue.AzureQueueManagerFactory aqmf = null;

	public AzureMessageQueueFactory(CloudMetadata metadata) {
		initProvider();
//		try {
//			this.aqmf = provider.createQueueManagerFactory(metadata.getPersistenceUnit(), metadata.getPersistenceInfo());
			this.info = metadata.getQueueMedatada();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (XMLStreamException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public CloudMessageQueue getQueue(String queueName) {
		queueName = queueName.toLowerCase();
		QueueInfo queueInfo = info.get(queueName);
		if (map == null) {
			map = new HashMap<String, AzureMessageQueue>();
		}
		if (map.containsKey(queueName))
			return map.get(queueName);
		if (queueInfo.getMode().equals(ModeQueue.PULL)) {
			AzureMessageQueue newqueue = new AzureMessageQueue(queueName, aqmf,
					queueInfo);
			map.put(queueName, newqueue);
			return newqueue;
		} else {
			return null;
		}
	}

	private void initProvider() {
		List<PersistenceProvider> list = getProviders();
		for (PersistenceProvider p : list) {
//			provider = (AzureProvider) p;
		}
	}

	private static List<PersistenceProvider> getProviders() {
		List<PersistenceProvider> list = new ArrayList<PersistenceProvider>();
		for (PersistenceProvider pp : PersistenceProviderResolverHolder
				.getPersistenceProviderResolver().getPersistenceProviders()) {
			list.add((PersistenceProvider) pp);
		}
		return list;

	}
}
