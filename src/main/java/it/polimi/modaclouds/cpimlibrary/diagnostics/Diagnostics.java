package it.polimi.modaclouds.cpimlibrary.diagnostics;

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


import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManager;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This is an {@code Entity} class used to store in the NoSQL service, objects that
 * contain the diagnostics of the application deployed to the cloud, like a log.
 * 
 * 
 */
@Entity(name = "Diagnostics")
public class Diagnostics {

	@Id
	private String id = null;
	private String logMsg = null;

	public Diagnostics() {

	}

	/**
	 * Create a {@code Diagnostics} entity and set the log passed as parameter
	 * 
	 * @param log String that contains the log that you want store.
	 */
	public Diagnostics(String log) {

		Long l = System.currentTimeMillis();
		this.id = l.toString();
		this.logMsg = log;

	}

	/**
	 * Returns the ID of the entity
	 * @return the ID of the entity
	 */
	public String getId() {
		return id;
	}
	//setter inutile che serve pero' per l'EntityManager
	public void setId(String id) {
		//this.id = id;
	}

	/**
	 * Returns the log message of the entity
	 * @return log message of the entity}
	 */
	public String getLogMsg() {
		return logMsg;
	}

	/**
	 * Assigns the log message to the entity
	 * 
	 * @param log log message
	 */
	public void setLogMsg(String log) {
		this.logMsg = log;
	}

	/**
	 * Stores the entity in the NoSQL service on the cloud.
	 * 
	 */
	public void save() {
		CloudEntityManager e;
		e = MF.getFactory().getEntityManagerFactory().createCloudEntityManager();
		e.persist(this);
	}

}
