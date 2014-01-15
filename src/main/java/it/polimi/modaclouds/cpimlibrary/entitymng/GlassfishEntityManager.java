package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

public class GlassfishEntityManager implements CloudEntityManager {

	EntityManager em = null;

	
	public GlassfishEntityManager(EntityManager em) {
		this.em = em;
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

	public void lock(Object entity, LockModeType lockMode) {
		em.lock(entity, lockMode);
	}

	public <T> T merge(T entity) {
		return em.merge(entity);
	}

	public void persist(Object entity) {

		EntityTransaction tx=em.getTransaction();
		tx.begin();
		em.persist(entity);
		tx.commit();
		close();
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
