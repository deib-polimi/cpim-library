package it.polimi.modaclouds.cpimlibrary.memcache;

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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.CacheNode;
import com.amazonaws.services.elasticache.model.CacheSecurityGroup;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersRequest;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersResult;
import com.amazonaws.services.elasticache.model.Endpoint;
import com.amazonaws.services.elasticache.model.ModifyCacheClusterRequest;

public class AmazonMemcache extends CloudMemcache {

	private AmazonElastiCacheClient ec = null;
	private final int expire_time = 60;
	private final int timeout = 5;
	private static MemcachedClient memCache = null;
	private CacheCluster cluster = null;
	
	private void start(String memcacheAddr) {
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(
			        getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ec = new AmazonElastiCacheClient(credentials);
		DescribeCacheClustersRequest describe_req = new DescribeCacheClustersRequest();
		describe_req.setShowCacheNodeInfo(true);
		DescribeCacheClustersResult describe_res = ec.describeCacheClusters(describe_req);
		List<CacheCluster> clusters = describe_res.getCacheClusters();
		for(CacheCluster c : clusters) {
			Endpoint conf_end = c.getConfigurationEndpoint();
			String addr = conf_end.getAddress() + ":" + conf_end.getPort();
			if(addr.equals(memcacheAddr)) {
				cluster = c;
				break;
			}
		}
		//setting security groups
		List<String> sec_groups_names = new ArrayList<String>();
		List<CacheSecurityGroup> res = ec.describeCacheSecurityGroups().getCacheSecurityGroups();
		for(int i = 0; i < res.size(); i++) {
			if(res.get(i).getDescription().contains("Cloud Platform Independent Model"))
				sec_groups_names.add(res.get(i).getCacheSecurityGroupName());
		}
		ModifyCacheClusterRequest req = new ModifyCacheClusterRequest(cluster.getCacheClusterId());
		req.setCacheSecurityGroupNames(sec_groups_names);
		req.setApplyImmediately(true);
		ec.modifyCacheCluster(req);
		//creation of memcached client
		List<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();
        if (cluster.getCacheNodes() != null ) {
            for (CacheNode node : cluster.getCacheNodes()) {
                    if (node != null) {
                    	Endpoint endpoint = node.getEndpoint();
                    if (endpoint != null && endpoint.getAddress() != null) {
                    	addrs.add(new InetSocketAddress(endpoint.getAddress(), endpoint.getPort()));
                    }
                }
            }
        }
        if(addrs.size() > 0) {
        	try {
        		System.out.println("Creation of memCacheClient.");
    			memCache = new MemcachedClient(addrs);
    			System.out.println("memCacheClient created.");
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        else
        	System.out.println("No available nodes.");
	}
	
	public AmazonMemcache(String memcacheAddr) {
		
		if (memCache == null) {
			this.start(memcacheAddr);
		}
		
	}
	
	@Override
	public void clearAll() {
		Future<Boolean> f = memCache.flush();
		try {
			f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e){
			f.cancel(false);
			return;
		}
	}

	@Override
	public Boolean contains(Object key) {
		
		Future<Object> f = memCache.asyncGet(key.toString());
		try {
			if(f.get(timeout, TimeUnit.SECONDS)!=null)
				return true;
		}
		catch (Exception e) {
			f.cancel(false);
			return false;
		}
		return false;
	}

	@Override
	public Boolean delete(Object key) {
		
		Future<Boolean> f = memCache.delete(key.toString());
		try {
			return f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return false;
		}
	}

	@Override
	public Boolean deleteAll(Collection<Object> keys) {
		
		for(Object key: keys){
			Future<Boolean> f = memCache.delete(key.toString());
			try {
				f.get(timeout, TimeUnit.SECONDS);
			}
			catch (Exception e) {
				f.cancel(false);
				return false;
			}
		}
		return true;
	}

	@Override
	public Object get(Object key) {
		
		Future<Object> f = memCache.asyncGet(key.toString());
		try {
			return f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return null;
		}
	}

	@SuppressWarnings("null")
	@Override
	public Map<Object, Object> getAll(Collection<Object> keys) {
		
		Map<Object, Object> ret = null;
		for(Object key: keys){
			Future<Object> f = memCache.asyncGet(key.toString());
			try {
				ret.put(key, f.get(timeout, TimeUnit.SECONDS));
			}
			catch (Exception e) {
				f.cancel(false);
				return null;
			}
		}
		return ret;
	}

	@Override
	public void put(Object key, Object value) {
		
		Future<Boolean> f = memCache.set(key.toString(), expire_time, value);
		try {
			f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return;
		}
	}

	@Override
	public void put(Object key, Object value, Integer expires) {
		
		Future<Boolean> f = memCache.set(key.toString(), expires, value);
		try {
			f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return;
		}
	}

	@Override
	public void putAll(Map<Object, Object> values) {
		
		Set<Object> keys = values.keySet();
		for(Object key: keys){
			Future<Boolean> f = memCache.set(key.toString(), expire_time, values.get(key.toString()));
			try {
				f.get(timeout, TimeUnit.SECONDS);
			}
			catch (Exception e) {
				f.cancel(false);
				return;
			}
		}
	}

	@Override
	public void putAll(Map<Object, Object> values, Integer expires) {
		
		Set<Object> keys = values.keySet();
		for(Object key: keys){
			Future<Boolean> f = memCache.set(key.toString(), expires, values.get(key.toString()));
			try {
				f.get(timeout, TimeUnit.SECONDS);
			}
			catch (Exception e) {
				f.cancel(false);
				return;
			}
		}
	}

	@Override
	public Long increment(Object key, long delta) {
		
		Future<Long> f = memCache.asyncIncr(key.toString(), delta);
		try {
			return f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return null;
		}
	}

	@Override
	public Long increment(Object key, long delta, Long initialValue) {

		if(this.contains(key.toString()))
			this.put(key, initialValue);
		
		Future<Long> f = memCache.asyncIncr(key.toString(), delta);
		try {
			return f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return null;
		}
	}

	@SuppressWarnings("null")
	@Override
	public Map<Object, Long> incrementAll(Collection<Object> keys, long delta) {
		
		Map<Object, Long> incremented = null;
		for(Object key: keys){
			Future<Long> f = memCache.asyncIncr(key.toString(), delta);
			try {
				f.get(timeout, TimeUnit.SECONDS);
				incremented.put(key, delta);
			}
			catch (Exception e) {
				f.cancel(false);
				return null;
			}
		}
		return incremented;
	}

	@SuppressWarnings("null")
	@Override
	public Map<Object, Long> incrementAll(Collection<Object> keys, long delta,
			Long initialValue) {

		Map<Object, Long> incremented = null;
		for(Object key: keys){
			this.increment(key, delta, initialValue);
			incremented.put(key, delta);
		}
		return incremented;
	}

	@SuppressWarnings("null")
	@Override
	public Map<Object, Long> incrementAll(Map<Object, Long> offsets) {
		try {
//			if(!this.isChacheClusterRunning()) return null;
			Set<Object> keys = offsets.keySet();
			Map<Object, Long> incremented = null;
			for(Object key: keys){
				this.increment(key.toString(), offsets.get(key));
				incremented.put(key, offsets.get(key));
			}
			return incremented;
		}
		catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("null")
	@Override
	public Map<Object, Long> incrementAll(Map<Object, Long> offsets,
			Long initialValue) {
		try {
//			if(!this.isChacheClusterRunning()) return null;
			Set<Object> keys = offsets.keySet();
			Map<Object, Long> incremented = null;
			for(Object key: keys){
				this.increment(key.toString(), offsets.get(key), initialValue);
				incremented.put(key, offsets.get(key));
			}
			return incremented;
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public void replace(Object key, Object newValue) {
		
		Future<Boolean> f = memCache.replace(key.toString(), expire_time, newValue);
		try {
			f.get(timeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			f.cancel(false);
			return;
		}
	}

	@Override
	public void replaceAll(Map<Object, Object> newvalues) {
		
		Set<Object> keys = newvalues.keySet();
		for(Object key: keys){
			Future<Boolean> f = memCache.replace(key.toString(), expire_time, newvalues.get(key.toString()));
			try {
				f.get(timeout, TimeUnit.SECONDS);
			}
			catch (Exception e) {
				f.cancel(false);
				return;
			}
		}
	}

}
