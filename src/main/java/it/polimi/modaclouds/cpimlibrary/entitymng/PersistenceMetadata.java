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
import java.util.Map;

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

    private PersistenceMetadata() {
        this.persistedClasses = new HashMap<>();
        populatePersistedClasses();
    }

    public static synchronized PersistenceMetadata getInstance() {
        if (instance == null) {
            instance = new PersistenceMetadata();
        }
        return instance;
    }

    public String getMappedClass(String name) {
        if (this.persistedClasses.isEmpty()) {
            throw new IllegalStateException("persistence.xml has not yet been parsed by CPIM");
        }
        return this.persistedClasses.get(name);
    }

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
            if (ReflectionUtils.isClassAnnotatedWith(clazz, Table.class)) {
                Table table = clazz.getAnnotation(Table.class);
                /* insert also <tableName, fullClassName> */
                persistedClasses.put(table.name(), className);
            }
            String[] elements = className.split("\\.");
            String simpleClassName = elements[elements.length - 1];
            /* insert <simpleClassName, fullClassName> */
            persistedClasses.put(simpleClassName, className);
        }
    }

    private void handleNamedQueries(Class<?> clazz) {
        if (ReflectionUtils.isClassAnnotatedWith(clazz, NamedQueries.class)) {
            /* multiple NamedQuery declared on the class */
            NamedQueries namedQueryList = clazz.getAnnotation(NamedQueries.class);
            for (NamedQuery namedQuery : namedQueryList.value()) {
                addNamedQuery(namedQuery);
            }
        } else if (ReflectionUtils.isClassAnnotatedWith(clazz, NamedQuery.class)) {
            /* single NamedQuery declared on the class */
            NamedQuery namedQuery = clazz.getAnnotation(NamedQuery.class);
            addNamedQuery(namedQuery);
        }
    }

    private void addNamedQuery(NamedQuery namedQuery) {
        if (namedQuery.query().toUpperCase().startsWith("UPDATE") || namedQuery.query().toUpperCase().startsWith("DELETE")) {
            namedQueries.put(namedQuery.name(), namedQuery.query().trim());
        }
    }
}
