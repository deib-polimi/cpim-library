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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Class that allows to create the {@code GoogleEntityManager} to manage
 * persistent objects using the Datastore service in Google App Engine.
 * 
 */

class GoogleEntityManagerFactory extends CloudEntityManagerFactory {

	private EntityManagerFactory factory = null;
	private String persistenceUnit = null;
	
	public GoogleEntityManagerFactory(String persistenceUnit) {
		this.persistenceUnit= persistenceUnit;
		factory = Persistence.createEntityManagerFactory(persistenceUnit);
	}
	@Override
	public CloudEntityManager createCloudEntityManager() {
		return new GoogleEntityManager(factory.createEntityManager());
	}
	@Override
	public void close(){
		if(persistenceUnit !=null){
		super.removeMF(persistenceUnit);
		factory.close();
		}
	}
}
