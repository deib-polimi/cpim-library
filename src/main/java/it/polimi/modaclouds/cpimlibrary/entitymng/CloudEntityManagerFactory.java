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
package it.polimi.modaclouds.cpimlibrary.entitymng;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CloudEntityManagerFactory {

	private EntityManagerFactory factory = null;

	public CloudEntityManagerFactory(String persistenceUnit,
			HashMap<String, String> persistenceInfo) {
		/*
		 * typeCloud parameter is not needed anymore since every cloud
		 * implementation will use Kundera as persistence provider, so there's
		 * no more need to instantiate different things at runtime based on
		 * cloud provider.
		 */
		factory = Persistence.createEntityManagerFactory(persistenceUnit);
	}

	public CloudEntityManager createCloudEntityManager() {
		return new CloudEntityManager(factory.createEntityManager());
	}

	public void close() {
		factory.close();
	}

}
