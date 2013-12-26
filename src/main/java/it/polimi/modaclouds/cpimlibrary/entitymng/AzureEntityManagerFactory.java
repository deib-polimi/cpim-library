package it.polimi.modaclouds.cpimlibrary.entitymng;

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


import java.util.Map;

import javax.persistence.Persistence;

/**
 * Class that allows to create the {@code GoogleEntityManager} to manage
 * persistent objects using the Table service in Windows Azure.
 * 
 */

class AzureEntityManagerFactory extends CloudEntityManagerFactory {

	private jpa4azure.impl.AzureEntityManagerFactory factory=null;
	private String persistenceUnit = null;
	
	public AzureEntityManagerFactory(String persistenceUnit){
		this.persistenceUnit=persistenceUnit;
		factory=(jpa4azure.impl.AzureEntityManagerFactory) Persistence.createEntityManagerFactory(persistenceUnit);
	}
	
	public AzureEntityManagerFactory(String persistenceUnit, @SuppressWarnings("rawtypes") Map map) {
		this.persistenceUnit=persistenceUnit;
		factory=(jpa4azure.impl.AzureEntityManagerFactory) Persistence.createEntityManagerFactory(persistenceUnit,map);
	}

	@Override
	public CloudEntityManager createCloudEntityManager() {
		return new AzureEntityManager((jpa4azure.impl.AzureEntityManager)factory.createEntityManager());
	}

	@Override
	public void close() {
		if(persistenceUnit !=null){
			super.removeMF(persistenceUnit);
			factory.close();
			}
		
	}

}
