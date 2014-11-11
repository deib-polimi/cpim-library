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
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.StatementBuilder;

import javax.persistence.*;
import java.util.*;

/**
 * Delegate every operation to the {@link javax.persistence.TypedQuery} implementation
 * of the runtime provider except for executeUpdate method.
 * <p/>
 * Every method that should return {@link javax.persistence.TypedQuery}, returns
 * <code>this</code> for chaining.
 *
 * @author Fabio Arcidiacono.
 * @see javax.persistence.TypedQuery
 */
public class TypedCloudQuery<X> implements TypedQuery<X> {

    private MigrationManager migrator;
    private final TypedQuery<X> query;

    public TypedCloudQuery(TypedQuery<X> query) {
        this.migrator = MigrationManager.getInstance();
        this.query = query;
    }

    @Override
    public List<X> getResultList() {
        return query.getResultList();
    }

    @Override
    public X getSingleResult() {
        return query.getSingleResult();
    }

    /**
     * In case of migration generate an Update or Delete statement
     * then send it to the migration system.
     * Otherwise delegates to the persistence provider implementation.
     *
     * @see javax.persistence.TypedQuery#executeUpdate()
     */
    @Override
    public int executeUpdate() {
        if (migrator.isMigrating()) {
            System.err.println("TypedCloudQuery.executeUpdate MIGRATION");
            Statement statement = StatementBuilder.generateUpdateDeleteStatement(query);
            migrator.propagate(statement);
            return 0;
        } else {
            System.err.println("TypedCloudQuery.executeUpdate DEFAULT");
            return query.executeUpdate();
        }
    }

    @Override
    public TypedQuery<X> setMaxResults(int maxResult) {
        query.setMaxResults(maxResult);
        return this;
    }

    @Override
    public int getMaxResults() {
        return query.getMaxResults();
    }

    @Override
    public TypedQuery<X> setFirstResult(int startPosition) {
        query.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return query.getFirstResult();
    }

    @Override
    public TypedQuery<X> setHint(String hintName, Object value) {
        query.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return query.getHints();
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
        query.setParameter(param, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Object value) {
        query.setParameter(name, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Object value) {
        query.setParameter(position, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return query.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return query.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return query.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return query.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return query.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return query.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return query.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return query.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return query.getParameterValue(position);
    }

    @Override
    public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
        query.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return query.getFlushMode();
    }

    @Override
    public TypedQuery<X> setLockMode(LockModeType lockMode) {
        query.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return query.getLockMode();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return query.unwrap(cls);
    }
}
