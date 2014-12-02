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
import lombok.Getter;
import lombok.Setter;
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
    @Getter private State normalState;
    @Getter private State migrationState;
    @Setter private State state;

    private MigrationManager() {
        this.normalState = new NormalState(this);
        this.migrationState = new MigrationState(this);
        this.state = normalState;
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
