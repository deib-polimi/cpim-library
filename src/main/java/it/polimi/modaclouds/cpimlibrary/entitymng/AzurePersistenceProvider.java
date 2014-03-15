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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import jpa4azure.impl.AzureProvider;

class AzurePersistenceProvider{

	@SuppressWarnings("unused")
	private AzureProvider ap=null;
	public AzurePersistenceProvider()
	{
	  List<PersistenceProvider> list=getProviders();
	  for(PersistenceProvider p:list)
	  {
		  if(p instanceof AzureProvider & ap==null)
		  {
			  ap=(AzureProvider) p;
		  }
	  }
	  if(ap==null)
		  System.out.println("no valid provider found");
	}
	
	
	private static List<PersistenceProvider> getProviders()
	{
		List<PersistenceProvider> list= new ArrayList<PersistenceProvider>();
		for(PersistenceProvider pp:PersistenceProviderResolverHolder.getPersistenceProviderResolver().getPersistenceProviders()){
			list.add((PersistenceProvider)pp);
			}
		return list;
	}
	
	public  CloudEntityManagerFactory createCloudEntityManagerFactory(String persistenceUnit) {
				return new AzureEntityManagerFactory(persistenceUnit);
		
	}

	public CloudEntityManagerFactory createCloudEntityManagerFactory(
			String persistenceUnit, @SuppressWarnings("rawtypes") Map <String,String> map) {
		return new AzureEntityManagerFactory(persistenceUnit,map);
	}
}



