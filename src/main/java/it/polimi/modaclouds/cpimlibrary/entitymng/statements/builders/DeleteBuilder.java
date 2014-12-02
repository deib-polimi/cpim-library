package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.DeleteStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.operators.CompareOperator;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class DeleteBuilder extends StatementBuilder {

    public DeleteBuilder() {
        super(Arrays.asList(CascadeType.ALL, CascadeType.REMOVE));
    }

    @Override
    protected Statement initStatement() {
        return new DeleteStatement();
    }

    @Override
    protected void onFiled(Statement statement, Object entity, Field field) {
        /* do nothing */
    }

    @Override
    protected void onRelationalField(Statement statement, Object entity, Field field) {
        /* do nothing */
    }

    @Override
    protected void onIdField(Statement statement, Object entity, Field idFiled) {
        String jpaColumnName = ReflectionUtils.getJPAColumnName(idFiled);
        Object idValue = ReflectionUtils.getValue(entity, idFiled);
        log.debug("id filed is {}, will be {} = {}", idFiled.getName(), jpaColumnName, idValue);
        statement.addCondition(jpaColumnName, CompareOperator.EQUAL, idValue);
    }

    @Override
    protected Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable) {
        String joinTableName = joinTable.name();
        String joinColumnName = joinTable.joinColumns()[0].name();
        Field joinColumnField = ReflectionUtils.getJoinColumnField(entity, joinColumnName);
        Object joinColumnValue = ReflectionUtils.getValue(entity, joinColumnField);

        Statement statement = initStatement();
        statement.setTable(joinTableName);
        statement.addCondition(joinColumnName, CompareOperator.EQUAL, joinColumnValue);

        log.debug("joinTable {}, condition: {} = {}", joinTableName, joinColumnName, joinColumnValue);
        return statement;
    }

    @Override
    protected Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable) {
        String joinTableName = joinTable.name();
        String inverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();
        Field idField = ReflectionUtils.getIdField(entity);
        Object entityId = ReflectionUtils.getValue(entity, idField);

        Statement statement = initStatement();
        statement.setTable(joinTableName);
        statement.addCondition(inverseJoinColumnName, CompareOperator.EQUAL, entityId);

        log.debug("joinTable {}, condition: {} = {}", joinTableName, inverseJoinColumnName, entityId);
        return statement;
    }
}
