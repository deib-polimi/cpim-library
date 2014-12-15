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
 */
public interface State {

    /**
     * Handle the start migration event.
     */
    public void startMigration();

    /**
     * Handle the stop migration event.
     */
    public void stopMigration();

    /**
     * Parse the query to generate the corresponding {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement}
     * or {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.DeleteStatement}.
     *
     * @param query the query to be parsed
     */
    public void propagate(Query query);

    /**
     * Use the given builder to build statements from the entity, then send them to the migration system.
     *
     * @param entity    entity to be parsed
     * @param operation one of {@link it.polimi.modaclouds.cpimlibrary.entitymng.migration.Operation}
     */
    public void propagate(Object entity, Operation operation);

    /**
     * Send the statement string representation to the migration system.
     *
     * @param statement the statement to be sent
     */
    public void propagate(Statement statement);
}
