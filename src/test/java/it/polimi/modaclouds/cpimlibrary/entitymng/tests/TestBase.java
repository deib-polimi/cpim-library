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
package it.polimi.modaclouds.cpimlibrary.entitymng.tests;

import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManagerFactory;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.OperationType;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.DeleteBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.InsertBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.UpdateBuilder;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.persistence.Query;
import java.util.Deque;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public abstract class TestBase {

    // CPIM stuff
    protected CloudEntityManagerFactory emf;
    protected CloudEntityManager em;
    protected MigrationManager migrator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        emf = MF.getFactory().getEntityManagerFactory();
        if (em != null && em.isOpen()) {
            em.close();
        }
        em = emf.createCloudEntityManager();
        migrator = MigrationManager.getInstance();
        migrator.startMigration();
    }

    @After
    public void tearDown() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
        migrator.stopMigration();
    }

    protected Deque<Statement> buildStatements(Object entity, OperationType operation) {
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
        return builder.build(entity);
    }

    protected Deque<Statement> buildStatements(Query query) {
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
        return builder.build(query, queryString);
    }

    /*---------------------------------------------------------------------------------*/
    /*-------------------------- UTILS, for debug purposes ----------------------------*/
    /*---------------------------------------------------------------------------------*/

    protected void clear() {
        em.clear();
        print("clear entity manager");
    }

    protected void print(String message) {
        if (log.isDebugEnabled()) {
            String delimiter = "--------------------------------------------------------------------";
            String spacing = message.length() <= 10 ? "\t\t\t\t\t\t\t  " : "\t\t\t\t\t\t";
            log.debug("\n" + delimiter + "\n" + spacing + message.toUpperCase() + "\n" + delimiter);
        } else {
            log.info("\t\t" + message.toUpperCase() + "\n");
        }
    }
}