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
package it.polimi.modaclouds.cpimlibrary.entitymng.migration;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Query;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage interaction with migration system and information about currently persisted class.
 * <p/>
 * Follow a state pattern, states are handled as an FSA.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.mffactory.MF
 * @see it.polimi.modaclouds.cpimlibrary.CloudMetadata
 */
@Slf4j
public class MigrationManager {

    private static MigrationManager instance = null;
    @Getter private State normalState;
    @Getter private State migrationState;
    @Setter private State state;
    private Map<String, String> persistedClasses = new HashMap<>();

    private MigrationManager() {
        this.normalState = new NormalState(this);
        this.migrationState = new MigrationState(this);
        this.state = normalState;
        populatePersistedClasses();
    }

    public static synchronized MigrationManager getInstance() {
        if (instance == null) {
            instance = new MigrationManager();
        }
        return instance;
    }

    public boolean isMigrating() {
        return this.state.equals(migrationState);
    }

    public void startMigration() {
        state.startMigration();
    }

    public void stopMigration() {
        state.stopMigration();
    }

    public void propagate(Query query) {
        state.propagate(query);
    }

    public void propagate(Object entity, StatementBuilder builder) {
        state.propagate(entity, builder);
    }

    public void propagate(Statement statement) {
        state.propagate(statement);
    }

    private void populatePersistedClasses() {
        log.info("map persisted class names to table names");
        Map<String, String> puInfo = MF.getFactory().getPersistenceUnitInfo();
        String[] classes = puInfo.get("classes").replace("[", "").replace("]", "").split(",");
        for (String className : classes) {
            className = className.trim();
            Class<?> clazz = ReflectionUtils.getClassInstance(className);
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

    public String getMappedClass(String name) {
        if (this.persistedClasses.isEmpty()) {
            throw new IllegalStateException("persistence.xml has not yet been parsed by CPIM");
        }
        return this.persistedClasses.get(name);
    }
}
