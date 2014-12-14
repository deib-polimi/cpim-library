package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.InsertStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class InsertBuilder extends StatementBuilder {

    public InsertBuilder() {
        super(Arrays.asList(CascadeType.ALL, CascadeType.PERSIST));
    }

    @Override
    protected Statement initStatement() {
        return new InsertStatement();
    }

    @Override
    protected void onFiled(Statement statement, Object entity, Field field) {
        super.addField(statement, entity, field);
    }

    @Override
    protected void onRelationalField(Statement statement, Object entity, Field field) {
        super.addRelationalFiled(statement, entity, field);
    }

    @Override
    protected void onIdField(Statement statement, Object entity, Field idFiled) {
        String fieldName = ReflectionUtils.getJPAColumnName(idFiled);
        Object fieldValue = ReflectionUtils.getValue(entity, idFiled);
        if (fieldValue == null) {
            String generatedId = generateId();
            log.info("generated Id for {} is {}", entity.getClass().getSimpleName(), generatedId);
            ReflectionUtils.setIdBackToEntity(entity, idFiled, generatedId);
            fieldValue = generatedId;
        }
        log.debug("{} will be {} = {}", idFiled.getName(), fieldName, fieldValue);
        statement.addField(fieldName, fieldValue);
    }

    @Override
    protected Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable) {
        String joinTableName = joinTable.name();
        String joinColumnName = joinTable.joinColumns()[0].name();
        String inverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();
        Field joinColumnField = ReflectionUtils.getJoinColumnField(entity, joinColumnName);
        Object joinColumnValue = ReflectionUtils.getValue(entity, joinColumnField);

        Statement statement = initStatement();
        statement.setTable(joinTableName);
        statement.addField(joinColumnName, joinColumnValue);

        Field inverseJoinColumnField = ReflectionUtils.getJoinColumnField(element, inverseJoinColumnName);
        Object inverseJoinColumnValue = ReflectionUtils.getValue(element, inverseJoinColumnField);
        statement.addField(inverseJoinColumnName, inverseJoinColumnValue);

        log.debug("joinTable {}, joinColumn {} = {}, inverseJoinColumn {} = {}", joinTableName, joinColumnName, joinColumnValue, inverseJoinColumnName, inverseJoinColumnValue);
        return statement;
    }

    @Override
    protected Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable) {
        /* do nothing */
        return null;
    }

    private String generateId() {return UUID.randomUUID().toString();}
}
