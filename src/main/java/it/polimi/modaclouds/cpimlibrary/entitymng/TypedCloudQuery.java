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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class TypedCloudQuery<X> implements TypedQuery<X> {

    private MigrationManager migrator;
    private final TypedQuery<X> query;
    @Getter private final String queryString;
    private Map<Parameter<?>, Object> parameters;

    public TypedCloudQuery(String queryString, TypedQuery<X> query) {
        this.migrator = MigrationManager.getInstance();
        this.parameters = new HashMap<>();
        this.queryString = queryString.trim();
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
            log.info("is MIGRATION state");
            migrator.propagate(this);
            return 0;
        } else {
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

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
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
        if (param == null) {
            throw new NullPointerException("parameter cannot be null");
        }
        parameters.put(param, value);
        query.setParameter(param, value);
        return this;
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Calendar parameters with temporal type are currently not supported");
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Calendar parameters with temporal type are currently not supported");
    }

    @Override
    public TypedQuery<X> setParameter(String name, Object value) {
        this.parameters.put(new CloudParameter<>(name, null, value.getClass()), value);
        query.setParameter(name, value);
        return this;
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Calendar parameters with temporal type are currently not supported");
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Date parameters with temporal type are currently not supported");
    }

    @Override
    public TypedQuery<X> setParameter(int position, Object value) {
        this.parameters.put(new CloudParameter<>(null, position, value.getClass()), value);
        query.setParameter(position, value);
        return this;
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Calendar parameters with temporal type are currently not supported");
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Date parameters with temporal type are currently not supported");
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return this.parameters.keySet();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        for (Parameter p : this.parameters.keySet()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        for (Parameter p : this.parameters.keySet()) {
            if (p.getName().equals(name) && p.getParameterType().equals(type)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Parameter<?> getParameter(int position) {
        for (Parameter p : this.parameters.keySet()) {
            if (p.getPosition().equals(position)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        for (Parameter p : this.parameters.keySet()) {
            if (p.getPosition().equals(position) && p.getParameterType().equals(type)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return query.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return (T) this.parameters.get(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return this.parameters.get(getParameter(name));
    }

    @Override
    public Object getParameterValue(int position) {
        return this.parameters.get(getParameter(position));
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
        query.setFlushMode(flushMode);
        return this;
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public FlushModeType getFlushMode() {
        return query.getFlushMode();
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public TypedQuery<X> setLockMode(LockModeType lockMode) {
        query.setLockMode(lockMode);
        return this;
    }

    // Note: Kundera[2.14] will throw UnsupportedOperationException()
    @Override
    public LockModeType getLockMode() {
        return query.getLockMode();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return query.unwrap(cls);
    }
}
