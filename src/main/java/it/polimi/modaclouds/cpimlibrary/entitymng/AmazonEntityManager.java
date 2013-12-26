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


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

public class AmazonEntityManager implements CloudEntityManager {

	private EntityManager em;
	private String packageName;
	
	public AmazonEntityManager(EntityManager em, String packageName)
	{
		this.em = em;
		this.packageName = packageName;
	}
	@Override
	public void clear() {
		
		em.clear();
	}

	@Override
	public void close() {
		em.close();
	}

	@Override
	public boolean contains(Object arg0) {
		return em.contains(arg0);
	}

	@Override
	public Query createNamedQuery(String arg0) {
		return em.createNamedQuery(changeFromField(arg0));
	}

	@Override
	public Query createNativeQuery(String arg0) {
		return em.createNativeQuery(changeFromField(arg0));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Query createNativeQuery(String arg0, Class arg1) {
		return em.createNativeQuery(changeFromField(arg0), arg1);
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		return em.createNativeQuery(changeFromField(arg0), arg1);
	}

	@Override
	public Query createQuery(String arg0) {
		return em.createQuery(changeFromField(arg0));
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1) {
		return em.find(arg0, arg1);
	}

	@Override
	public void flush() {
		em.flush();
	}

	@Override
	public Object getDelegate() {
		return em.getDelegate();
	}

	@Override
	public FlushModeType getFlushMode() {
		return em.getFlushMode();
	}

	@Override
	public <T> T getReference(Class<T> arg0, Object arg1) {
		return em.getReference(arg0, arg1);
	}

	@Override
	public EntityTransaction getTransaction() {
		return em.getTransaction();
	}

	@Override
	public boolean isOpen() {
		return em.isOpen();
	}

	@Override
	public void joinTransaction() {
		em.joinTransaction();
	}

	@Override
	public void lock(Object arg0, LockModeType arg1) {
		em.lock(arg0, arg1);
	}

	@Override
	public <T> T merge(T arg0) {
		return em.merge(arg0);
	}

	@Override
	public void persist(Object arg0) {
		em.persist(arg0);
		close();
	}

	@Override
	public void refresh(Object arg0) {
		em.refresh(arg0);
	}

	@Override
	public void remove(Object arg0) {
		em.remove(arg0);
	}

	@Override
	public void setFlushMode(FlushModeType arg0) {
		em.setFlushMode(arg0);
	}
	
	private String changeFromField(String arg0) {
		
		System.out.println(arg0);
		//raccolgo il nome della classe
		int beginIndex = arg0.indexOf("FROM") + 5;
		String field = arg0.substring(beginIndex);
		System.out.println(field);
		if(field.contains(" ")) {
			int endIndex = field.indexOf(" ");
			field = field.substring(0, endIndex);
			System.out.println(field);
		}
		
		//devo aggiungere al nome della classe, il nome del pacchetto
		String result = arg0.replace(field, packageName + "." + field);
		System.out.println(result);
		return result;
	}

}
