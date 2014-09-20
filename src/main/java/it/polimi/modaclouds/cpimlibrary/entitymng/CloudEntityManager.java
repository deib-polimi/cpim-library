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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

public class CloudEntityManager {

	EntityManager em = null;

	public CloudEntityManager(EntityManager entityManager) {
		this.em = entityManager;
	}

	public void clear() {
		em.clear();
	}

	public void close() {
		em.close();
	}

	public boolean contains(Object entity) {
		return em.contains(entity);
	}

	public Query createNamedQuery(String name) {
		return em.createNamedQuery(name);
	}

	public Query createNativeQuery(String sqlString) {
		return em.createNativeQuery(sqlString);
	}

	public Query createNativeQuery(String sqlString,
			@SuppressWarnings("rawtypes") Class resultClass) {
		return em.createNativeQuery(sqlString, resultClass);
	}
	
	// Kundera EntityManagerImpl throws NotImplementedException on this
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return em.createNativeQuery(sqlString, resultSetMapping);
	}


	public Query createQuery(String qlString) {
		return em.createQuery(qlString);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return em.find(entityClass, primaryKey);
	}

	public void flush() {
		em.flush();
	}

	public Object getDelegate() {
		return em.getDelegate();
	}

	public FlushModeType getFlushMode() {
		return em.getFlushMode();
	}

	// Kundera EntityManagerImpl throws NotImplementedException on this
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return em.getReference(entityClass, primaryKey);
	}

	public EntityTransaction getTransaction() {
		return em.getTransaction();
	}

	public boolean isOpen() {
		return em.isOpen();
	}

	public void joinTransaction() {
		em.joinTransaction();
	}

	// Kundera EntityManagerImpl throws NotImplementedException on this
	public void lock(Object entity, LockModeType lockMode) {
		em.lock(entity, lockMode);
	}

	public <T> T merge(T entity) {
		return em.merge(entity);
	}

	public void persist(Object entity) {
		em.persist(entity);
		close(); // TODO why this?
	}

	public void refresh(Object entity) {
		em.refresh(entity);
	}

	public void remove(Object entity) {
		em.remove(entity);
	}

	public void setFlushMode(FlushModeType flushMode) {
		em.setFlushMode(flushMode);
	}

}
