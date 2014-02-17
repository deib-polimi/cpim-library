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
package it.polimi.modaclouds.cpimlibrary.memcache;

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.exception.NotSupportedVendorException;

import java.util.Collection;
import java.util.Map;

/**
 * This abstract class exposes the method used to manage the Memcache service in
 * an independent-platform way. To instantiate this class you need to use the
 * {@code CloudMemcache.getCloudMemcache()} method.
 */
public abstract class CloudMemcache {

	private static CloudMemcache instance = null;

	/**
	 * This method instances a CloudMemcache object with the singleton
	 * technique, in an independent-platform way, passing the CloudMetadata
	 * object.
	 * 
	 * @param metadata
	 * @return a CloudMemcache instance
	 * @throws NotSupportedVendorException
	 *             if the vendor string is not "Azure" or "Google"
	 */
	public static CloudMemcache getCloudMemcache(CloudMetadata metadata) {
		if (instance == null) {
			if (metadata.getTypeCloud().equals("Azure")) {
				instance = new AzureMemcache(metadata.getMemcacheAddr());
			} else if (metadata.getTypeCloud().equals("Google")) {
				instance = new GoogleMemcache();
			} else if (metadata.getTypeCloud().equals("Amazon")) {
				instance = new AmazonMemcache(metadata.getMemcacheAddr());
			}
			//include caso glassfish
			else if (metadata.getTypeCloud().equals("Glassfish")) {
				instance = new GlassfishMemcache(metadata.getMemcacheAddr());
			} else
				try {
					throw new NotSupportedVendorException("The vendor "
							+ metadata.getTypeCloud()
							+ " is not supported by the library");
				} catch (NotSupportedVendorException e) {
					e.printStackTrace();
				}
		}
		return instance;
	}

	/**
	 * If this method was called than the next call of
	 * {@code getCloudMemcache} method returns a new instance.
	 */
	public void close() {
		instance = null;
	}

	/**
	 * Clear all the cache content
	 * 
	 * @throws IllegalStateException
	 */
	public abstract void clearAll();

	/**
	 * Checks if exists in the cache an element by the key passed as parameter
	 * 
	 * @param key
	 * @return {@code true} if exists the key, {@code false} otherwise.
	 */
	public abstract Boolean contains(Object key);

	/**
	 * Delete the key in the cache, this method returns even if the key does not
	 * exist in the cache.
	 * 
	 * @param key
	 * @return {@code true} if the key is deleted, {@code false} otherwise.
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract Boolean delete(Object key);

	/**
	 * Delete all the keys contained in the Collection parameter, this method
	 * returns even if the key does not exist in the cache.
	 * 
	 * @param keys
	 *            - Collection that contains the keys to delete
	 * @return {@code true} if the keys are deleted, {@code false} otherwise.
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract Boolean deleteAll(Collection<Object> keys);

	/**
	 * Returns an object that represents the value associated with the given
	 * key.
	 * 
	 * 
	 * @param key
	 * @return the value of the given key (null if the key does not exist).
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract Object get(Object key);

	/**
	 * Returns a Map containing the pair key-value of the given Collection of
	 * keys.
	 * 
	 * @param keys
	 * @return a Map key-value relating to the given keys
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract Map<Object, Object> getAll(java.util.Collection<Object> keys);

	/**
	 * Put the key-value element in the cache with the default expiration time,
	 * iff key does not already exist.
	 * 
	 * @param key
	 * @param value
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract void put(Object key, Object value);

	/**
	 * Put the key-value element in the cache with the given expiration time,
	 * iff the key does not already exist.
	 * 
	 * @param key
	 * @param value
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract void put(Object key, Object value, Integer expires); // Da
																			// integer
																			// a
	// Expiration di
	// GAE!!

	/**
	 * Puts the key-value elements contained in the given Map in the cache with
	 * the default expiration time, iff the key does not already exist.
	 * 
	 * @param values
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract void putAll(java.util.Map<Object, Object> values);

	/**
	 * Puts the key-value elements contained in the given Map in the cache with
	 * the given expiration time, iff the key does not already exist.
	 * 
	 * @param values
	 * @param expires
	 * @throws IllegalStateException
	 *             in the rare circumstance where queue is too full to accept
	 *             any more requests
	 */
	public abstract void putAll(java.util.Map<Object, Object> values,
			Integer expires);

	public abstract Long increment(Object key, long delta);

	public abstract Long increment(Object key, long delta, Long initialValue);

	public abstract Map<Object, Long> incrementAll(Collection<Object> keys,
			long delta);

	public abstract Map<Object, Long> incrementAll(Collection<Object> keys,
			long delta, Long initialValue);

	public abstract Map<Object, Long> incrementAll(Map<Object, Long> offsets);

	public abstract Map<Object, Long> incrementAll(Map<Object, Long> offsets,
			Long initialValue);

	/**
	 * Replaces the value corresponding to the given key with the
	 * {@code newValue}. Do nothing if the key does not exist.
	 * 
	 * @param key
	 *            - object to replace
	 * @param newValue
	 */
	public abstract void replace(Object key, Object newValue);

	/**
	 * Performs the replace operation for each key-value pair. Values are the
	 * newValues that will be stored. Do nothing if the key does not exist.
	 * 
	 * @param newvalues
	 *            - Map containing the key-value pair
	 */
	public abstract void replaceAll(Map<Object, Object> newvalues);

}
