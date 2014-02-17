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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.IdentifiableValue;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

class GoogleMemcache extends CloudMemcache {

	MemcacheService memCache = null; // memcache sincrona,

	public GoogleMemcache() {
		memCache = MemcacheServiceFactory.getMemcacheService();
	}

	@Override
	public void clearAll() {
		memCache.clearAll();
	}

	@Override
	public Boolean contains(Object key) {
		return memCache.contains(key);
	}

	@Override
	public Boolean delete(Object key) {
		return memCache.delete(key);
	}

	@Override
	public Boolean deleteAll(Collection<Object> keys) {
		memCache.deleteAll(keys);
		return true;
	}

	@Override
	public Object get(Object key) {

		return memCache.get(key);
	}

	@Override
	public Map<Object, Object> getAll(Collection<Object> keys) {
		return memCache.getAll(keys);
	}

	@Override
	public void put(Object key, Object value) {
		memCache.put(key, value);

	}

	@Override
	public void put(Object key, Object value, Integer expires) {
		Expiration e = Expiration.byDeltaSeconds(expires);
		memCache.put(key, value, e);
	}

	@Override
	public void putAll(Map<Object, Object> values) {
		memCache.putAll(values);

	}

	@Override
	public void putAll(Map<Object, Object> values, Integer expires) {
		Expiration e = Expiration.byDeltaSeconds(expires);
		memCache.putAll(values, e);

	}

	@Override
	public Long increment(Object key, long delta) {
		return memCache.increment(key, delta);

	}

	@Override
	public Long increment(Object key, long delta, Long initialValue) {
		return memCache.increment(key, delta, initialValue);
	}

	@Override
	public void replace(Object key, Object newValue) {
		IdentifiableValue oldValue = memCache.getIdentifiable(key);
		memCache.putIfUntouched(key, oldValue, newValue);
	}

	@Override
	public void replaceAll(Map<Object, Object> newvalues) {
		Map<Object, IdentifiableValue> oldValues = memCache
				.getIdentifiables(newvalues.keySet());
		Map<Object, MemcacheService.CasValues> newValues = new HashMap<Object, MemcacheService.CasValues>();
		MemcacheService.CasValues cas = null;
		for (Object o : newvalues.keySet()) {
			cas = new MemcacheService.CasValues(oldValues.get(o),
					newvalues.get(o));
			newValues.put(o, cas);
		}
		memCache.putIfUntouched(newValues);
	}

	@Override
	public Map<Object, Long> incrementAll(Collection<Object> keys, long delta) {
		return memCache.incrementAll(keys, delta);
	}

	@Override
	public Map<Object, Long> incrementAll(Collection<Object> keys, long delta,
			Long initialValue) {
		return memCache.incrementAll(keys, delta, initialValue);
	}

	@Override
	public Map<Object, Long> incrementAll(Map<Object, Long> offsets) {
		return memCache.incrementAll(offsets);
	}

	@Override
	public Map<Object, Long> incrementAll(Map<Object, Long> offsets,
			Long initialValue) {
		return memCache.incrementAll(offsets, initialValue);
	}
}