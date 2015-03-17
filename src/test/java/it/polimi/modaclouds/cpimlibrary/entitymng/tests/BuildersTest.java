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

import it.polimi.modaclouds.cpimlibrary.entitymng.entities.*;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.OperationType;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.DeleteStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.InsertStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.BuildersConfiguration;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.Filter;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.LogicOperator;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Deque;
import java.util.Iterator;

/**
 * @author Fabio Arcidiacono.
 */
public class BuildersTest extends TestBase {

    @Test
    public void joinTableTest() {
        ProjectMTM project = new ProjectMTM();
        project.setName("Project 1");

        EmployeeMTM employee = new EmployeeMTM();
        employee.setName("Fabio");
        employee.setSalary(123L);
        employee.addProjects(project);
        Deque<Statement> statements;

        print("insert project");
        statements = buildStatements(project, OperationType.INSERT);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("ProjectMTM", statement.getTable());
        Iterator<Filter> fieldsIterator = statement.getFieldsIterator();
        Filter filter = fieldsIterator.next();
        Assert.assertEquals("PROJECT_ID", filter.getColumn());
        Object projId = filter.getValue();
        filter = fieldsIterator.next();
        Assert.assertEquals("NAME", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Project 1", filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());
        Assert.assertFalse(statement.getConditionsIterator().hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("insert employee");
        statements = buildStatements(employee, OperationType.INSERT);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("EmployeeMTM", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
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
        // Assert.assertFalse(fieldsIterator.hasNext());
        Assert.assertFalse(statement.getConditionsIterator().hasNext());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("EMPLOYEE_PROJECT", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        filter = fieldsIterator.next();
        Assert.assertEquals("EMPLOYEE_ID", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(empId, filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("PROJECT_ID", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(projId, filter.getValue());
        Assert.assertFalse(fieldsIterator.hasNext());
        Assert.assertFalse(statement.getConditionsIterator().hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("update project");
        statements = buildStatements(project, OperationType.UPDATE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("ProjectMTM", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        Assert.assertEquals("PROJECT_ID", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(projId, filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("NAME", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Project 1", filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());
        Iterator<Object> conditionsIterator = statement.getConditionsIterator();
        Object condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PROJECT_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(projId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());
        Assert.assertTrue(statements.isEmpty());

        print("update employee");
        statements = buildStatements(employee, OperationType.UPDATE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("EmployeeMTM", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        filter = fieldsIterator.next();
        Assert.assertEquals("NAME", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Fabio", filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("SALARY", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(123L, filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("delete project");
        statements = buildStatements(project, OperationType.DELETE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("EMPLOYEE_PROJECT", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PROJECT_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(projId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("ProjectMTM", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PROJECT_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(projId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        print("delete employee");
        statements = buildStatements(employee, OperationType.DELETE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("EMPLOYEE_PROJECT", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("EmployeeMTM", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());
    }

    @Test
    public void queryTest() {
        Employee employee = new Employee();
        employee.setName("Fabio");
        employee.setSalary(123L);

        Deque<Statement> statements;

        print("update");
        TypedQuery<Employee> typedQuery = em.createQuery("UPDATE Employee e SET e.salary = :s, e.name = :n2 WHERE e.name = :n OR e.salary <> :s2", Employee.class);
        typedQuery.setParameter("s", 789L);
        typedQuery.setParameter("s2", 123L);
        typedQuery.setParameter("n2", "Pippo");
        typedQuery.setParameter("n", "Fabio");
        statements = buildStatements(typedQuery);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("Employee", statement.getTable());
        Iterator<Filter> fieldsIterator = statement.getFieldsIterator();
        Filter filter = fieldsIterator.next();
        Assert.assertEquals("SALARY", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(789L, filter.getValue());
        filter = fieldsIterator.next();
        Assert.assertEquals("NAME", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Pippo", filter.getValue());
        Assert.assertFalse(fieldsIterator.hasNext());
        Iterator<Object> conditionsIterator = statement.getConditionsIterator();
        Object condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("NAME", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals("Fabio", ((Filter) condition).getValue());
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof LogicOperator);
        Assert.assertEquals(LogicOperator.OR, condition);
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("SALARY", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.NOT_EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(123L, ((Filter) condition).getValue());
        Assert.assertTrue(statements.isEmpty());

        print("delete");
        Query query = em.createQuery("DELETE FROM Employee e WHERE e.name = :n AND e.salary >= :s", Employee.class);
        query.setParameter("n", "Pippo");
        query.setParameter("s", 123L);
        statements = buildStatements(query);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("Employee", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("NAME", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals("Pippo", ((Filter) condition).getValue());
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof LogicOperator);
        Assert.assertEquals(LogicOperator.AND, condition);
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("SALARY", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.GREATER_THAN_OR_EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(123L, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());
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
        Assert.assertFalse(statement.getConditionsIterator().hasNext());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
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
        Assert.assertFalse(statement.getConditionsIterator().hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("update following cascade");
        statements = buildStatements(employee, OperationType.UPDATE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("Phone", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
        filter = fieldsIterator.next();
        Assert.assertEquals("NUMBER", filter.getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, filter.getOperator());
        Assert.assertEquals(123456789L, filter.getValue());
        // Assert.assertFalse(fieldsIterator.hasNext());
        Iterator<Object> conditionsIterator = statement.getConditionsIterator();
        Object condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PHONE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(phoneId, ((Filter) condition).getValue());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
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
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("delete following cascade");
        statements = buildStatements(employee, OperationType.DELETE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(2, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("Phone", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("PHONE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(phoneId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());
    }

    @Test
    public void noFollowCascadeTest() {
        Phone phone = new Phone();
        phone.setNumber(123456789L);

        EmployeeOTO employee = new EmployeeOTO();
        employee.setName("Fabio");
        employee.setSalary(123L);
        employee.setPhone(phone);

        Deque<Statement> statements;
        BuildersConfiguration.getInstance().doNotFollowCascades();

        print("insert NO following cascade");
        statements = buildStatements(employee, OperationType.INSERT);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof InsertStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        Iterator<Filter> fieldsIterator = statement.getFieldsIterator();
        Filter filter = fieldsIterator.next();
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
        // Assert.assertFalse(fieldsIterator.hasNext());
        Assert.assertFalse(statement.getConditionsIterator().hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("update NO following cascade");
        statements = buildStatements(employee, OperationType.UPDATE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof UpdateStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        fieldsIterator = statement.getFieldsIterator();
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
        // Assert.assertFalse(fieldsIterator.hasNext());
        Iterator<Object> conditionsIterator = statement.getConditionsIterator();
        Object condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());

        print("delete NO following cascade");
        statements = buildStatements(employee, OperationType.DELETE);
        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        statement = statements.removeFirst();
        Assert.assertTrue(statement instanceof DeleteStatement);
        Assert.assertEquals("EmployeeOTOne", statement.getTable());
        Assert.assertFalse(statement.getFieldsIterator().hasNext());
        conditionsIterator = statement.getConditionsIterator();
        condition = conditionsIterator.next();
        Assert.assertTrue(condition instanceof Filter);
        Assert.assertEquals("EMPLOYEE_ID", ((Filter) condition).getColumn());
        Assert.assertEquals(CompareOperator.EQUAL, ((Filter) condition).getOperator());
        Assert.assertEquals(empId, ((Filter) condition).getValue());
        Assert.assertFalse(conditionsIterator.hasNext());

        Assert.assertTrue(statements.isEmpty());
    }
}
