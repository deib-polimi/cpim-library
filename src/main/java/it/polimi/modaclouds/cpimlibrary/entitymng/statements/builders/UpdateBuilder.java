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
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Token;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Builder for UPDATE statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder
 */
@Slf4j
public class UpdateBuilder extends StatementBuilder {

    /**
     * Read the builder configuration and instantiate the builder accordingly.
     *
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.BuildersConfiguration
     */
    public UpdateBuilder() {
        super();
        if (BuildersConfiguration.getInstance().isFollowingCascades()) {
            super.followCascades(Arrays.asList(CascadeType.ALL, CascadeType.MERGE));
        }
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#initStatement()
     */
    @Override
    protected Statement initStatement() {
        return new UpdateStatement();
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#onFiled(it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement, Object, java.lang.reflect.Field)
     */
    @Override
    protected void onFiled(Statement statement, Object entity, Field field) {
        super.addField(statement, entity, field);
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#onRelationalField(it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement, Object, java.lang.reflect.Field)
     */
    @Override
    protected void onRelationalField(Statement statement, Object entity, Field field) {
        super.addRelationalFiled(statement, entity, field);
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#onIdField(it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement, Object, java.lang.reflect.Field)
     */
    @Override
    protected void onIdField(Statement statement, Object entity, Field idFiled) {
        String jpaColumnName = ReflectionUtils.getJPAColumnName(idFiled);
        Object idValue = ReflectionUtils.getFieldValue(entity, idFiled);
        log.debug("id filed is {}, will be {} = {}", idFiled.getName(), jpaColumnName, idValue);
        statement.addCondition(jpaColumnName, CompareOperator.EQUAL, idValue);
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#generateJoinTableStatement(Object, Object, javax.persistence.JoinTable)
     */
    @Override
    protected Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable) {
        /* no need to update joinTable */
        return null;
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#generateInverseJoinTableStatement(Object, javax.persistence.JoinTable)
     */
    @Override
    protected Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable) {
        /* no need to update joinTable */
        return null;
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#handleQuery(javax.persistence.Query, java.util.ArrayList)
     */
    @Override
    protected Statement handleQuery(Query query, ArrayList<Token> tokens) {
        Iterator<Token> itr = tokens.iterator();
        String objectParam = "";
        boolean wherePart = false;
        Statement statement = new UpdateStatement();
        while (itr.hasNext()) {
            Token current = itr.next();
            switch (current.type) {
                case SET:
                case WHITESPACE:
                    /* fall through */
                    break;
                case UPDATE:
                    super.setTableName(itr, statement);
                    objectParam = super.nextTokenOfType(TokenType.STRING, itr);
                    log.debug("JPQL object parameter is {}", objectParam);
                    break;
                case WHERE:
                    wherePart = true;
                    break;
                case COLUMN:
                    String column = super.getJPAColumnName(current, objectParam, statement.getTable());
                    String operator = super.nextTokenOfType(TokenType.COMPAREOP, itr);
                    Object value = super.getNextParameterValue(itr, query);
                    log.debug("found column will be {} {} {}", column, operator, value);
                    if (wherePart) {
                        statement.addCondition(column, operator, value);
                    } else {
                        /* is in the SET part */
                        statement.addField(column, value);
                    }
                    break;
                case LOGICOP:
                    log.debug("found logic operator {}", current.data);
                    statement.addCondition(current.data);
            }
        }
        return statement;
    }
}
