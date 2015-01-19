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

import it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.DeleteBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.InsertBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.UpdateBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Query;
import java.util.Deque;

/**
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager
 */
@Slf4j
public class MigrationState implements State {

    private MigrationManager manager;

    public MigrationState(MigrationManager manager) {
        this.manager = manager;
    }

    /* (non-Javadoc)
     *
     * @see State#startMigration()
     */
    @Override
    public void startMigration() {
        throw new IllegalStateException("Migration already in progress");
    }

    /* (non-Javadoc)
     *
     * @see State#stopMigration()
     */
    @Override
    public void stopMigration() {
        log.info("Stopping migration") ;
        manager.setState(manager.getNormalState());
    }

    /* (non-Javadoc)
     *
     * @see State#propagate(javax.management.Query)
     */
    @Override
    public void propagate(Query query) {
        String queryString;
        if (query instanceof CloudQuery) {
            queryString = ((CloudQuery) query).getQueryString();
        } else if (query instanceof TypedCloudQuery) {
            queryString = ((TypedCloudQuery) query).getQueryString();
        } else {
            throw new RuntimeException("Query has not been wrapped by CPIM");
        }
        StatementBuilder builder;
        if (queryString.startsWith("UPDATE")) {
            builder = new UpdateBuilder();
        } else if (queryString.startsWith("DELETE")) {
            builder = new DeleteBuilder();
        } else {
            throw new RuntimeException("Query is neither UPDATE nor DELETE");
        }
        Deque<Statement> statements = builder.build(query, queryString);
        propagate(statements);
    }

    /* (non-Javadoc)
     *
     * @see State#propagate(Object, it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder)
     */
    @Override
    public void propagate(Object entity, OperationType operation) {
        StatementBuilder builder;
        switch (operation) {
            case INSERT:
                builder = new InsertBuilder();
                break;
            case UPDATE:
                builder = new UpdateBuilder();
                break;
            case DELETE:
                builder = new DeleteBuilder();
                break;
            default:
                throw new RuntimeException("Operation type: " + operation + " not recognized");
        }
        Deque<Statement> statements = builder.build(entity);
        propagate(statements);
    }

    private void propagate(Deque<Statement> statements) {
        while (!statements.isEmpty()) {
            propagate(statements.removeFirst());
        }
    }

    private void propagate(Statement statement) {
        // TODO send to migration system
        log.info(statement.toString());
    }
}
