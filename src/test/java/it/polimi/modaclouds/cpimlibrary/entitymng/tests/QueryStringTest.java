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

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.DeleteStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.InsertStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Fabio Arcidiacono.
 */
public class QueryStringTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInsert() {
        InsertStatement insert = new InsertStatement();
        insert.setTable("Test");
        insert.addField("name", "Fabio");
        insert.addField("salary", 123L);
        Assert.assertEquals("INSERT INTO Test (name, salary) VALUES ('Fabio', '123')", insert.toString());

        try {
            insert.addCondition("name", "=", "Fabio");
        } catch (UnsupportedOperationException e) {
            // that's fine
        }
        try {
            insert.addCondition("name", CompareOperator.NOT_EQUAL, "Fabio");
        } catch (UnsupportedOperationException e) {
            // that's fine
        }
        try {
            insert.addCondition("OR");
        } catch (UnsupportedOperationException e) {
            // that's fine
        }
    }

    @Test
    public void testUpdate() {
        UpdateStatement update = new UpdateStatement();
        update.setTable("Test");
        update.addField("name", "Fabio");
        update.addField("salary", 123L);
        Assert.assertEquals("UPDATE Test SET name = 'Fabio', salary = '123'", update.toString());

        update = new UpdateStatement();
        update.setTable("Test");
        update.addField("name", "Fabio");
        update.addCondition("salary", CompareOperator.GREATER_THAN_OR_EQUAL, 45L);
        update.addCondition("OR");
        update.addCondition("name", CompareOperator.NOT_EQUAL, "Fabio");
        Assert.assertEquals("UPDATE Test SET name = 'Fabio' WHERE salary >= '45' OR name <> 'Fabio'", update.toString());
    }

    @Test
    public void testDelete() {
        DeleteStatement delete = new DeleteStatement();
        delete.setTable("Test");
        Assert.assertEquals("DELETE FROM Test", delete.toString());

        delete = new DeleteStatement();
        delete.setTable("Test");
        delete.addCondition("salary", CompareOperator.LOWER_THAN_OR_EQUAL, 45L);
        delete.addCondition("AND");
        delete.addCondition("name", "<>", "Fabio");
        Assert.assertEquals("DELETE FROM Test WHERE salary <= '45' AND name <> 'Fabio'", delete.toString());

        thrown.expect(UnsupportedOperationException.class);
        delete.addField("name", "Fabio");
    }
}
