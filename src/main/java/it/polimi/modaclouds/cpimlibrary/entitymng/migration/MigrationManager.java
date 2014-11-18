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

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;

/**
 * Manage interaction with migration system.
 *
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class MigrationManager {

    private static MigrationManager instance = null;
    private boolean isMigrating = false;

    private MigrationManager() {}

    public static synchronized MigrationManager getInstance() {
        if (instance == null) {
            instance = new MigrationManager();
        }
        return instance;
    }

    public boolean isMigrating() {
        return this.isMigrating;
    }

    // TODO ask to some api
    public void startMigration() {
        this.isMigrating = true;
    }

    public void stopMigration() {
        this.isMigrating = false;
    }

    public void propagate(Deque<Statement> statements) {
        while (!statements.isEmpty()) {
            propagate(statements.removeFirst());
        }
    }

    public void propagate(Statement statement) {
        // TODO send to migration system
        log.info(statement.toString());
    }
}
