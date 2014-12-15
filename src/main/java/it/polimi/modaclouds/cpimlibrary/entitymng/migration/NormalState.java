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

import javax.persistence.Query;

/**
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager
 */
public class NormalState implements State {

    private MigrationManager manager;

    public NormalState(MigrationManager manager) {
        this.manager = manager;
    }

    /* (non-Javadoc)
     * @see State#startMigration()
     */
    @Override
    public void startMigration() {
        manager.setState(manager.getMigrationState());
    }

    /* (non-Javadoc)
     * @see State#stopMigration()
     */
    @Override
    public void stopMigration() {
        throw new IllegalStateException("Migration was not started yet");
    }

    /* (non-Javadoc)
     * @see State#propagate(javax.management.Query)
     */
    @Override
    public void propagate(Query query) {
        throw new IllegalStateException("Migration was not started yet");
    }

    /* (non-Javadoc)
     * @see State#propagate(Object, it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder)
     */
    @Override
    public void propagate(Object entity, Operation operation) {
        throw new IllegalStateException("Migration was not started yet");
    }

    /* (non-Javadoc)
     * @see State#propagate(it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement)
     */
    @Override
    public void propagate(Statement statement) {
        throw new IllegalStateException("Migration was not started yet");
    }
}
