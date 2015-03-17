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

import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.OperationType;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberProvider;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Delegate every operation to the {@link javax.persistence.EntityManager} implementation
 * of the runtime provider except for persist, merge, remove and createQuery methods.
 *
 * @author Fabio Arcidiacono.
 * @see javax.persistence.EntityManager
 */
@Slf4j
public class CloudEntityManager implements EntityManager {

    private MigrationManager migrant;
    private EntityManager delegate;

    public CloudEntityManager(EntityManager entityManager) {
        this.migrant = MF.getFactory().getCloudMetadata().useMigration() ? MigrationManager.getInstance() : null;
        this.delegate = entityManager;
    }

    /**
     * In case of migration generates a SELECT statement
     * then sends it to the migration system.
     * Otherwise delegates to the persistence provider implementation.
     *
     * @see javax.persistence.EntityManager#persist(Object)
     */
    @Override
    public void persist(Object entity) {
        if (migrant != null) {
            if (migrant.isMigrating()) {
                log.info("is MIGRATION state");
                migrant.propagate(entity, OperationType.INSERT);
            } else {
                String tableName = ReflectionUtils.getJPATableName(entity);
                int id = SeqNumberProvider.getInstance().getNextSequenceNumber(tableName);
                Field idField = ReflectionUtils.getIdField(entity);
                ReflectionUtils.setEntityField(entity, idField, String.valueOf(id));
                delegate.persist(entity);
            }
        } else {
            delegate.persist(entity);
        }
    }

    /**
     * In case of migration generates an UPDATE statements
     * then sends it to the migration system.
     * Otherwise delegates to the persistence provider implementation.
     *
     * @see javax.persistence.EntityManager#merge(Object)
     */
    @Override
    public <T> T merge(T entity) {
        if (migrant != null && migrant.isMigrating()) {
            log.info("is MIGRATION state");
            migrant.propagate(entity, OperationType.UPDATE);
            return entity;
        } else {
            return delegate.merge(entity);
        }
    }

    /**
     * In case of migration generates an DELETE statements
     * then sends it to the migration system.
     * Otherwise delegates to the persistence provider implementation.
     *
     * @see javax.persistence.EntityManager#remove(Object)
     */
    @Override
    public void remove(Object entity) {
        if (migrant != null && migrant.isMigrating()) {
            log.info("is MIGRATION state");
            migrant.propagate(entity, OperationType.DELETE);
        } else {
            delegate.persist(entity);
        }
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return delegate.find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return delegate.find(entityClass, primaryKey, properties);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return delegate.find(entityClass, primaryKey, lockMode);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return delegate.find(entityClass, primaryKey, lockMode, properties);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return delegate.getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        delegate.setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return delegate.getFlushMode();
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public void lock(Object entity, LockModeType lockMode) {
        delegate.lock(entity, lockMode);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {
        delegate.refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        delegate.refresh(entity, properties);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        delegate.refresh(entity, lockMode);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void detach(Object entity) {
        delegate.detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return delegate.contains(entity);
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public LockModeType getLockMode(Object entity) {
        return delegate.getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        delegate.setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    /**
     * Delegates query generation to the persistence provider
     * then returns a wrapped query type.
     *
     * @see javax.persistence.EntityManager#createQuery(String)
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery
     */
    @Override
    public Query createQuery(String queryString) {
        log.debug("CloudEntityManager.createQuery WRAPPING");
        return new CloudQuery(queryString, delegate.createQuery(queryString));
    }

    /**
     * Delegates query generation to the persistence provider
     * then returns a wrapped query type.
     *
     * @see javax.persistence.EntityManager#createQuery(String, Class)
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery
     */
    @Override
    public <T> TypedQuery<T> createQuery(String queryString, Class<T> resultClass) {
        log.debug("CloudEntityManager.createQuery WRAPPING");
        return new TypedCloudQuery<>(queryString, delegate.createQuery(queryString, resultClass));
    }

    /**
     * Delegates query generation to the persistence provider
     * then returns a wrapped query type.
     *
     * @see javax.persistence.EntityManager#createNamedQuery(String)
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery
     */
    @Override
    public Query createNamedQuery(String name) {
        log.debug("CloudEntityManager.createNamedQuery WRAPPING");
        String queryString = PersistenceMetadata.getInstance().getNamedQuery(name);
        return new CloudQuery(queryString, delegate.createNamedQuery(name));
    }

    /**
     * Delegates query generation to the persistence provider
     * then returns a wrapped query type.
     *
     * @see javax.persistence.EntityManager#createNamedQuery(String, Class)
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery
     */
    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        log.debug("CloudEntityManager.createNamedQuery WRAPPING");
        String queryString = PersistenceMetadata.getInstance().getNamedQuery(name);
        return new TypedCloudQuery<>(queryString, delegate.createNamedQuery(name, resultClass));
    }

    @Override
    public Query createNativeQuery(String queryString) {
        throw new UnsupportedOperationException("Native queries are not supported");
    }

    @Override
    public Query createNativeQuery(String queryString, Class resultClass) {
        throw new UnsupportedOperationException("Native queries are not supported");
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public Query createNativeQuery(String queryString, String resultSetMapping) {
        throw new UnsupportedOperationException("Native queries are not supported");
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        throw new UnsupportedOperationException("Criteria queries are currently not supported");
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        throw new UnsupportedOperationException("Criteria queries are currently not supported");
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        throw new UnsupportedOperationException("Criteria queries are currently not supported");
    }

    // Note: Kundera[2.14] just return null
    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        throw new UnsupportedOperationException("Stored Procedure queries are currently not supported");
    }

    // Note: Kundera[2.14] just return null
    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        throw new UnsupportedOperationException("Stored Procedure queries are currently not supported");
    }

    // Note: Kundera[2.14] just return null
    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        throw new UnsupportedOperationException("Stored Procedure queries are currently not supported");
    }

    // Note: Kundera[2.14] just return null
    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        throw new UnsupportedOperationException("Stored Procedure queries are currently not supported");
    }

    @Override
    public void joinTransaction() {
        delegate.joinTransaction();
    }

    // Note: Kundera[2.14] just return false
    @Override
    public boolean isJoinedToTransaction() {
        return delegate.isJoinedToTransaction();
    }

    // Note: Kundera[2.14] will throw NotImplementedException()
    @Override
    public <T> T unwrap(Class<T> cls) {
        return delegate.unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return delegate.getDelegate();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return delegate.getTransaction();
    }

    /**
     * Use of this method is discouraged due to possibility of escaping from migration control.
     *
     * @return the entityManagerFactory of the runtime persistence provider
     */
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        log.warn("get EntityManagerFactory from CloudEntityManager");
        return delegate.getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return delegate.getMetamodel();
    }

    // Note: Kundera[2.14] just return null
    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return delegate.createEntityGraph(rootType);
    }

    // Note: Kundera[2.14] just return null
    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return delegate.createEntityGraph(graphName);
    }

    // Note: Kundera[2.14] just return null
    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return delegate.getEntityGraph(graphName);
    }

    // Note: Kundera[2.14] just return null
    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return delegate.getEntityGraphs(entityClass);
    }
}
