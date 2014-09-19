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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.NotSupportedVendorException;
import it.polimi.modaclouds.cpimlibrary.exception.ParserConfigurationFileException;

import java.util.HashMap;
import java.util.Map;

/**
 * Expose the methods to create the {@code CloudEntityManager} to manage the
 * NoSQL service in an independent-platform way. To instantiate this class you need
 * to use the {@code CloudEntityManagerFactory.getCloudEntityManagerFactory} method.
 * 
 */
public abstract class CloudEntityManagerFactory {

	private static HashMap<String, CloudEntityManagerFactory> map = new HashMap<String, CloudEntityManagerFactory>();

	/**
	 * This method instances a CloudEntityManagerFactory class in a static way.
	 * It receives the <i>vendor</i> and the name of the <i>persistenceUnit</i>
	 * that you want managed.
	 * 
	 * <p>
	 * If it passed the String like {@code "Azure"} or {@code "Google"} this
	 * method becomes cloud dependent, but if it passed the <i>vendor</i>
	 * contained in the CloudMetadata class calling the {@code getTypeCloud()}
	 * method, the method becomes platform-independent.
	 * </p>
	 * 
	 * @param vendor
	 *            - "Azure" or "Google" string or the content of the
	 *            CloudMetadata.getTypeCloud() return.
	 * @param persistenceUnit
	 *            - name of the persistence unit to manage.
	 * @return a CloudEntityManagerFactory instance with the singleton technique
	 *         with respect to the persistence unit name.
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudEntityManagerFactory getCloudEntityManagerFactory(
			String vendor, String persistenceUnit) {
		CloudEntityManagerFactory emf = null;
		if (map.containsKey(persistenceUnit))
			return map.get(persistenceUnit);

		if (vendor.equals("Azure")) {
			//AzurePersistenceProvider cp = new AzurePersistenceProvider();
			//emf = (AzureEntityManagerFactory) cp
			//		.createCloudEntityManagerFactory(persistenceUnit, null);
			emf = new AzureEntityManagerFactory(persistenceUnit);
			map.put(persistenceUnit, emf);
		} else if (vendor.equals("Google")) {
			emf = new GoogleEntityManagerFactory(persistenceUnit);
			map.put(persistenceUnit, emf);
		} else if (vendor.equals("Amazon")) {
			try {
				emf = new AmazonEntityManagerFactory(persistenceUnit, 
						CloudMetadata.getCloudMetadata().getPersistenceInfo().get("entity.package"));
			} catch (ParserConfigurationFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put(persistenceUnit, emf);
		}
		//aggiunto caso glassfish
		else if (vendor.equals("Glassfish")) {
			emf = new GlassfishEntityManagerFactory(persistenceUnit);
			map.put(persistenceUnit, emf);
		}  else
			try {
				throw new NotSupportedVendorException("The vendor " + vendor
						+ " is not supported by the library");
			} catch (NotSupportedVendorException e) {
				e.printStackTrace();
			}
		return emf;
	}

	/**
	 * This method instances a CloudEntityManagerFactory class in a static way.
	 * It receives the <i>vendor</i> and the name of the <i>persistenceUnit</i>
	 * that you want managed, and some additional properties info.
	 * 
	 * <p>
	 * If it passed the String like {@code "Azure"} or {@code "Google"} this
	 * method becomes cloud dependent, but if it passed the <i>vendor</i>
	 * contained in the CloudMetadata class calling the {@code getTypeCloud()}
	 * method, the method becomes platform-independent.
	 * 
	 * 
	 * @param vendor
	 *            - "Azure" or "Google" string or the content of the
	 *            CloudMetadata.getTypeCloud() return.
	 * @param persistenceUnit
	 *            - name of the persistence unit to manage.
	 * @param properties
	 *            - A map that contains some additional info
	 * @return a CloudEntityManagerFactory instance with the singleton technique
	 *         with respect to the persistence unit name.
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudEntityManagerFactory getCloudEntityManagerFactory(
			String vendor, String persistenceUnit,
			@SuppressWarnings("rawtypes") Map properties) {
		CloudEntityManagerFactory emf = null;
		if (map.containsKey(persistenceUnit))
			return map.get(persistenceUnit);

		if (vendor.equals("Azure")) {
//			AzurePersistenceProvider cp = new AzurePersistenceProvider();
//			emf = (AzureEntityManagerFactory) cp
//					.createCloudEntityManagerFactory(persistenceUnit,
//							properties);
			emf = new AzureEntityManagerFactory(persistenceUnit);
			map.put(persistenceUnit, emf);
		} else if (vendor.equals("Google")) {
			emf = new GoogleEntityManagerFactory(persistenceUnit);
			map.put(persistenceUnit, emf);
		} else if (vendor.equals("Amazon")) {
			String packageName = properties.get("entity.package").toString();
			emf = new AmazonEntityManagerFactory(persistenceUnit, packageName);
			System.out.println("EMF " + emf);
			map.put(persistenceUnit, emf);
		}
		//aggiunto caso glassfish
		else if (vendor.equals("Glassfish")) {
			emf = new GlassfishEntityManagerFactory(persistenceUnit);
			map.put(persistenceUnit, emf);
		} else
			try {
				throw new NotSupportedVendorException("The vendor " + vendor
						+ " is not supported by the library");
			} catch (NotSupportedVendorException e) {
				e.printStackTrace();
			}
		return emf;
	}

	/**
	 * This method instances a CloudEntityManagerFactory class in a static way.
	 * It receives the CloudMetadata object to instantiate the specific factory.
	 * 
	 * <p>
	 * This method is platform-independent
	 * </p>
	 * 
	 * @param metadata
	 * @return a CloudEntityManagerFactory instance with the singleton technique
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudEntityManagerFactory getCloudEntityManagerFactory(
			CloudMetadata metadata) {
		CloudEntityManagerFactory emf = null;
		if (map.containsKey(metadata.getPersistenceUnit()))
			return map.get(metadata.getPersistenceUnit());

		if (metadata.getTypeCloud().equals("Azure")) {
			//AzurePersistenceProvider cp = new AzurePersistenceProvider();
			//emf = (AzureEntityManagerFactory) cp
			//		.createCloudEntityManagerFactory(
			//				metadata.getPersistenceUnit(),
			//				metadata.getPersistenceInfo());
			emf = new AzureEntityManagerFactory(metadata.getPersistenceUnit());
			map.put(metadata.getPersistenceUnit(), emf);
		} else if (metadata.getTypeCloud().equals("Google")) {
			emf = new GoogleEntityManagerFactory(metadata.getPersistenceUnit());
			map.put(metadata.getPersistenceUnit(), emf);
		} else if (metadata.getTypeCloud().equals("Amazon")) {
			emf = new AmazonEntityManagerFactory(metadata.getPersistenceUnit(),
					metadata.getPersistenceInfo().get("entity.package").toString());
			map.put(metadata.getPersistenceUnit(), emf);
		}
		//aggiunto caso glassfish
		else if (metadata.getTypeCloud().equals("Glassfish")) {
			emf = new GlassfishEntityManagerFactory(metadata.getPersistenceUnit());
			map.put(metadata.getPersistenceUnit(), emf);
		}  else
			try {
				throw new NotSupportedVendorException("The vendor "
						+ metadata.getTypeCloud()
						+ " is not supported by the library");
			} catch (NotSupportedVendorException e) {
				e.printStackTrace();
			}
		return emf;
	}

	/**
	 * This method close the CloudEntityManagerFactory instance.
	 */
	public abstract void close();

	void removeMF(String persistenceUnit) {
		map.remove(persistenceUnit);
	}

	/**
	 * Create a {@code CloudEntityManager} instance used to manage the NoSQL
	 * service.
	 * 
	 * @return an instance of {@code CloudEntityManager}
	 * @see CloudEntityManager
	 */
	public abstract CloudEntityManager createCloudEntityManager();
}
