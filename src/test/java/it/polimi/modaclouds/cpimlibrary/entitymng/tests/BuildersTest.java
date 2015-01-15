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

import it.polimi.modaclouds.cpimlibrary.entitymng.entities.EmployeeOTO;
import it.polimi.modaclouds.cpimlibrary.entitymng.entities.Phone;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.OperationType;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.DeleteStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.InsertStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.BuildersConfiguration;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.Filter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Deque;
import java.util.Iterator;

/**
 * @author Fabio Arcidiacono.
 */
public class BuildersTest extends TestBase {

    @Test
    public void joinTableTest() {
        // TODO
    }

    @Test
    public void queryTest() {
        // TODO
    }

    @Test
    public void followCascadeTest() {
        Phone phone = new Phone();
        phone.setNumber(123456789L);

        EmployeeOTO employee = new EmployeeOTO();
        employee.setName("Fabio");
        employee.setSalary(123L);
        employee.setPhone(phone);

        Deque<Statement> statements;
        BuildersConfiguration.getInstance().followCascades();

        print("insert following cascade");
        statements = buildStatements(employee, OperationType.INSERT);

        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        Statement statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("Phone", statement.getTable());
        Iterator<Filter> fieldsIterator = statement.getFieldsIterator();
        Assert.assertTrue(fieldsIterator.hasNext());
        Filter filter = fieldsIterator.next();
        Assert.assertEquals("PHONE_ID", filter.getColumn());
        Object phoneId = filter.getValue();
        filter = fieldsIterator.next();
        Assert.assertEquals("NUMBER", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(123456789L, filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        Assert.assertTrue(fieldsIterator.hasNext());
        filter = fieldsIterator.next();
        Assert.assertEquals("EMPLOYEE_ID", filter.getColumn());
        Object empId = filter.getValue();
        filter = fieldsIterator.next();
        Assert.assertEquals("NAME", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Fabio", filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("SALARY", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(123L, filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("PHONE_ID", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(phoneId, filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("update following cascade");
        statements = buildStatements(employee, OperationType.UPDATE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("Phone", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        Assert.assertTrue(fieldsIterator.hasNext());
        filter = fieldsIterator.next();
        Assert.assertEquals("NUMBER", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(123456789L, filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());
        Iterator<Object> conditionsIterator = statement.getConditionsIterator();
        Assert.assertTrue(conditionsIterator.hasNext());
        Object condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PHONE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(phoneId, ((Filter) condition).getValue());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        Assert.assertTrue(fieldsIterator.hasNext());
        filter = fieldsIterator.next();
        Assert.assertEquals("NAME", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Fabio", filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("SALARY", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(123L, filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("PHONE_ID", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(phoneId, filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());
        conditionsIterator = statement.getConditionsIterator();
        Assert.assertTrue(conditionsIterator.hasNext());
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertTrue(statements.isEmpty());

        Assert.assertTrue(statements.isEmpty());

        print("delete following cascade");
        statements = buildStatements(employee, OperationType.DELETE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("Phone", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        Assert.assertFalse(fieldsIterator.hasNext());
        conditionsIterator = statement.getConditionsIterator();
        Assert.assertTrue(conditionsIterator.hasNext());
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PHONE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(phoneId, ((Filter) condition).getValue());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        Assert.assertFalse(fieldsIterator.hasNext());
        conditionsIterator = statement.getConditionsIterator();
        Assert.assertTrue(conditionsIterator.hasNext());
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertTrue(statements.isEmpty());

        Assert.assertTrue(statements.isEmpty());
    }
}
