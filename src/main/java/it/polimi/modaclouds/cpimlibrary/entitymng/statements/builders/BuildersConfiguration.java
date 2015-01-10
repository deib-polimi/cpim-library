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
package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains configurations for statement builders, provides methods to set and read configuration.
 * <p/>
 * Configuration is in singleton, the first call builds the configuration that reads persistence metadata
 * from {@link it.polimi.modaclouds.cpimlibrary.CloudMetadata} and build class mapping.
 * <p/>
 * By default cascade types are not followed, behavior can be changed at runtime by calling
 * {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.BuildersConfiguration#setFollowCascades(boolean)}.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 * @see it.polimi.modaclouds.cpimlibrary.CloudMetadata
 * @see it.polimi.modaclouds.cpimlibrary.mffactory.MF
 */
@Slf4j
public class BuildersConfiguration {

    private static BuildersConfiguration instance = null;
    private Map<String, String> persistedClasses;
    @Setter private boolean followCascades;

    private BuildersConfiguration() {
        this.persistedClasses = new HashMap<>();
        this.followCascades = false;
        populatePersistedClasses();
    }

    public static synchronized BuildersConfiguration getInstance() {
        if (instance == null) {
            instance = new BuildersConfiguration();
        }
        return instance;
    }

    public boolean followCascades() {
        return this.followCascades;
    }

    public String getMappedClass(String name) {
        if (this.persistedClasses.isEmpty()) {
            throw new IllegalStateException("persistence.xml has not yet been parsed by CPIM");
        }
        return this.persistedClasses.get(name);
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
}
