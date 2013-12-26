package it.polimi.modaclouds.cpimlibrary.memcache;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

public class GlassfishMemcache extends CloudMemcache {

	MemcachedClient memCache = null;
	public GlassfishMemcache(String memcacheAddr)
	{
		try {
			//Per deploy in Azure
			//"localhost_WorkerRole1:11211"
//			PER LOCALHOST
//			AddrUtil.getAddresses("127.0.0.1:11211")
			memCache = new MemcachedClient(AddrUtil.getAddresses(memcacheAddr));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void clearAll() {
		memCache.flush();
	}

	@Override
	public Boolean contains(Object key) {
		String keyString=String.valueOf(key.hashCode());
		Object o=memCache.get(keyString);
		if(o==null)
			return false;
		else
			return true;
	}

	@Override
	public Boolean delete(Object key) {
		String keyString=String.valueOf(key.hashCode());
		try {
			OperationFuture<Boolean> o=memCache.delete(keyString);
			return o.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Boolean deleteAll(Collection<Object> keys) {
		String keyString=null;
		for(Object o: keys)
		{
		keyString=String.valueOf(o.hashCode());
		memCache.delete(keyString);
		}
		
		return true; 
	}

	@Override
	public Object get(Object key) {
		String keyString=String.valueOf(key.hashCode());
		return memCache.get(keyString);
	}

	@Override
	public Map<Object, Object> getAll(Collection<Object> keys) {
		Map<Object, Object> m=new HashMap<Object, Object>(); 
		String keyString=null;
		for(Object o: keys)
		{
			keyString=String.valueOf(o.hashCode());
			Object value=memCache.get(keyString);
			m.put(o,value);
		}
        return m;
		
	}

	@Override
	public void put(Object key, Object value) {
		String keyString=String.valueOf(key.hashCode());
		memCache.add(keyString, 3600, value);
	}

	@Override
	public void put(Object key, Object value, Integer expires) {
		String keyString=String.valueOf(key.hashCode());
		memCache.add(keyString, expires, value);		
	}

	@Override
	public void putAll(Map<Object, Object> values) {
		String keyString=null;
		Object value=null;
		for(Object key: values.keySet())
		{
			value=values.get(key);
			keyString=String.valueOf(key.hashCode());
			memCache.add(keyString, 3600, value);
		}
	}

	@Override
	public void putAll(Map<Object, Object> values, Integer expires) {
		String keyString=null;
		Object value=null;
		for(Object key: values.keySet())
		{
			value=values.get(key);
			keyString=String.valueOf(key.hashCode());
			memCache.add(keyString, expires, value);
		}
	}

	@Override
	public Long increment(Object key, long delta) {
		String keyString=String.valueOf(key.hashCode());
		return memCache.incr(keyString, delta);	
	}

	@Override
	public void replace(Object key, Object newValue) {
		String keyString=String.valueOf(key.hashCode());
		memCache.replace(keyString,3600,newValue);
	}

	@Override
	public Long increment(Object key, long delta, Long initialValue) {
		String keyString=String.valueOf(key.hashCode());
		return memCache.incr(keyString, delta, initialValue);
	}

	@Override
	public void replaceAll(Map<Object, Object> newvalues) {
		for(Object o:newvalues.keySet())
		{
			replace(o,newvalues.get(o));
		}
		
	}

	@Override
	public Map<Object, Long> incrementAll(Collection<Object> keys, long delta) {
		Map<Object, Long> m=new HashMap<Object, Long>();
		String keyString=null;
		Long value=null;
		for(Object o:keys)
		{
			keyString=String.valueOf(o.hashCode());
			value=memCache.incr(keyString, delta);
			m.put(o,value);
		}
		return m;	
	}

	@Override
	public Map<Object, Long> incrementAll(Collection<Object> keys, long delta,
			Long initialValue) {
		Map<Object, Long> m=new HashMap<Object, Long>();
		String keyString=null;
		Long value=null;
		for(Object o:keys)
		{
			keyString=String.valueOf(o.hashCode());
			value=memCache.incr(keyString, delta,initialValue);
			m.put(o,value);
		}
		return m;
	}

	@Override
	public Map<Object, Long> incrementAll(Map<Object, Long> offsets) {
		Map<Object, Long> m=new HashMap<Object, Long>();
		String keyString=null;
		Long value=null;
		for(Object o: offsets.keySet())
		{
			keyString=String.valueOf(o.hashCode());
			value=memCache.incr(keyString, offsets.get(o));
			m.put(o,value);
		}
		return m;
	}

	@Override
	public Map<Object, Long> incrementAll(Map<Object, Long> offsets,
			Long initialValue) {
		Map<Object, Long> m=new HashMap<Object, Long>();
		String keyString=null;
		Long value=null;
		for(Object o: offsets.keySet())
		{
			keyString=String.valueOf(o.hashCode());
			value=memCache.incr(keyString, offsets.get(o),initialValue);
			m.put(o,value);
		}
		return m;
	}
}
