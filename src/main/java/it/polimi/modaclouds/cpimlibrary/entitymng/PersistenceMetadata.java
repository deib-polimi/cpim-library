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

import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maintains metadata obtained from persistence.xml.
 * <p/>
 * Class is in singleton, the first call builds the metadata reading persistence metadata
 * from {@link it.polimi.modaclouds.cpimlibrary.CloudMetadata} building class mapping
 * and named queries mapping.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.CloudMetadata
 * @see it.polimi.modaclouds.cpimlibrary.mffactory.MF
 */
@Slf4j
public class PersistenceMetadata {

    private static PersistenceMetadata instance = null;
    private Map<String, String> persistedClasses;
    private Map<String, String> namedQueries;
    private Set<String> tables;

    private PersistenceMetadata() {
        this.persistedClasses = new HashMap<>();
        this.namedQueries = new HashMap<>();
        this.tables = new HashSet<>();
        populatePersistedClasses();
    }

    public static synchronized PersistenceMetadata getInstance() {
        if (instance == null) {
            instance = new PersistenceMetadata();
        }
        return instance;
    }

    /**
     * Returns a set containing the table names of the persisted POJO.
     *
     * @return a set of table name
     */
    public Set<String> getPersistedTables() {
        return tables;
    }

    /**
     * Returns the full class name of the POJO associated to {@code name}.
     *
     * @param name a table name or a simple class name
     *
     * @return the full qualified class name
     */
    public String getMappedClass(String name) {
        if (this.persistedClasses.isEmpty()) {
            throw new IllegalStateException("persistence.xml has not yet been parsed by CPIM");
        }
        return this.persistedClasses.get(name);
    }

    /**
     * Returns the JPQL string representation of the named query
     * identified by {@code name}.
     *
     * @param name the name of the named query
     *
     * @return the query string
     */
    public String getNamedQuery(String name) {
        if (this.namedQueries.isEmpty()) {
            throw new IllegalStateException("persistence.xml has not yet been parsed by CPIM");
        }
        return this.namedQueries.get(name);
    }

    private void populatePersistedClasses() {
        log.info("map persisted class names to table names");
        Map<String, String> puInfo = MF.getFactory().getPersistenceUnitInfo();
        String[] classes = puInfo.get("classes").replace("[", "").replace("]", "").split(",");
        for (String className : classes) {
            className = className.trim();
            Class<?> clazz = ReflectionUtils.getClassInstance(className);
            handleNamedQueries(clazz);
            handlePersistedClasses(className, clazz);
        }
    }

    private void handleNamedQueries(Class<?> clazz) {
        if (ReflectionUtils.isClassAnnotatedWith(clazz, NamedQueries.class)) {
            /* multiple NamedQuery declared on the class */
            NamedQueries namedQueryList = clazz.getAnnotation(NamedQueries.class);
            for (NamedQuery namedQuery : namedQueryList.value()) {
                namedQueries.put(namedQuery.name(), namedQuery.query().trim());
            }
        } else if (ReflectionUtils.isClassAnnotatedWith(clazz, NamedQuery.class)) {
            /* single NamedQuery declared on the class */
            NamedQuery namedQuery = clazz.getAnnotation(NamedQuery.class);
            namedQueries.put(namedQuery.name(), namedQuery.query().trim());
        }
    }

    private void handlePersistedClasses(String className, Class<?> clazz) {
        String[] elements = className.split("\\.");
        String simpleClassName = elements[elements.length - 1];
        /* insert mapping <simpleClassName, fullClassName> */
        persistedClasses.put(simpleClassName, className);
        if (ReflectionUtils.isClassAnnotatedWith(clazz, Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            if (table != null && !table.name().equals("")) {
                /* insert also mapping <tableName, fullClassName> */
                persistedClasses.put(table.name(), className);
                tables.add(table.name());
            } else {
                tables.add(simpleClassName);
            }
        } else {
            tables.add(simpleClassName);
        }
    }
}
