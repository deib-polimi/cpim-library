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

import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

/**
 * Class used to interact with the persistence context in the cloud, using the
 * Table service on the Windows Azure platform environment.
 * 
 * <p>
 * The AzureEntityManager is used to create and remove persistent entity
 * instances, to find entities by their primary key, and to query over entities,
 * on the Windows Azure Platform.
 * </p>
 * <p>
 * The set of entities that can be managed by a given AzureEntityManager
 * instance is defined by a persistence unit. A persistence unit defines the set
 * of all classes that are related or grouped by the application, and which must
 * be colocated in their mapping to a single database.
 * </p>
 * 
 * @since Java Persistence API 1.0
 * 
 */
class AzureEntityManager implements CloudEntityManager {

	jpa4azure.impl.AzureEntityManager aem = null;

	public AzureEntityManager() {
		aem = new jpa4azure.impl.AzureEntityManager(null, null, null);
	}

	public AzureEntityManager(jpa4azure.impl.AzureEntityManager em) {
		aem = em;
	}

	public void clear() {
		aem.clear();
	}

	public void close() {
		aem.close();
	}

	public boolean contains(Object entity) {
		return aem.contains(entity);
	}

	public Query createNamedQuery(String name) {
		return aem.createNamedQuery(name);
	}

	public Query createNativeQuery(String sqlString) {
		return aem.createNativeQuery(sqlString);
	}

	public Query createNativeQuery(String sqlString,
			@SuppressWarnings("rawtypes") Class resultClass) {
		return aem.createNativeQuery(sqlString, resultClass);
	}

	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return aem.createNativeQuery(sqlString, resultSetMapping);
	}

	public Query createQuery(String qlString) {

		return aem.createQuery(qlString);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return aem.find(entityClass, primaryKey);
	}

	public void flush() {
		aem.flush();
	}

	public Object getDelegate() {
		return aem.getDelegate();
	}

	public FlushModeType getFlushMode() {
		return aem.getFlushMode();
	}

	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return aem.getReference(entityClass, primaryKey);
	}

	public EntityTransaction getTransaction() {
		return aem.getTransaction();
	}

	public boolean isOpen() {
		return aem.isOpen();
	}

	public void joinTransaction() {
		aem.joinTransaction();
	}

	public void lock(Object entity, LockModeType lockMode) {
		aem.lock(entity, lockMode);
	}

	public <T> T merge(T entity) {
		return aem.merge(entity);
	}

	public void persist(Object entity) {
		aem.persist(entity);
	}

	public void refresh(Object entity) {
		aem.refresh(entity);
	}

	public void remove(Object entity) {
		aem.remove(entity);
	}

	public void setFlushMode(FlushModeType flushMode) {
		aem.setFlushMode(flushMode);
	}

}
