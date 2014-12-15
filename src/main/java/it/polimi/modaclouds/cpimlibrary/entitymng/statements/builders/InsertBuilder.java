package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.InsertStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Token;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Builder for INSERT statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder
 */
@Slf4j
public class InsertBuilder extends StatementBuilder {

    public InsertBuilder() {
        super(Arrays.asList(CascadeType.ALL, CascadeType.PERSIST));
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#initStatement()
     */
    @Override
    protected Statement initStatement() {
        return new InsertStatement();
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
        String fieldName = ReflectionUtils.getJPAColumnName(idFiled);
        Object fieldValue = ReflectionUtils.getFieldValue(entity, idFiled);
        if (fieldValue == null) {
            String generatedId = generateId();
            log.info("generated Id for {} is {}", entity.getClass().getSimpleName(), generatedId);
            ReflectionUtils.setIdBackToEntity(entity, idFiled, generatedId);
            fieldValue = generatedId;
        }
        log.debug("{} will be {} = {}", idFiled.getName(), fieldName, fieldValue);
        statement.addField(fieldName, fieldValue);
    }

    private String generateId() {return UUID.randomUUID().toString();}

    /* (non-Javadoc)
     *
     * @see StatementBuilder#generateJoinTableStatement(Object, Object, javax.persistence.JoinTable)
     */
    @Override
    protected Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable) {
        String joinTableName = joinTable.name();
        String joinColumnName = joinTable.joinColumns()[0].name();
        String inverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();
        Field joinColumnField = ReflectionUtils.getJoinColumnField(entity, joinColumnName);
        Object joinColumnValue = ReflectionUtils.getFieldValue(entity, joinColumnField);

        Statement statement = initStatement();
        statement.setTable(joinTableName);
        statement.addField(joinColumnName, joinColumnValue);

        Field inverseJoinColumnField = ReflectionUtils.getJoinColumnField(element, inverseJoinColumnName);
        Object inverseJoinColumnValue = ReflectionUtils.getFieldValue(element, inverseJoinColumnField);
        statement.addField(inverseJoinColumnName, inverseJoinColumnValue);

        log.debug("joinTable {}, joinColumn {} = {}, inverseJoinColumn {} = {}", joinTableName, joinColumnName, joinColumnValue, inverseJoinColumnName, inverseJoinColumnValue);
        return statement;
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#generateInverseJoinTableStatement(Object, javax.persistence.JoinTable)
     */
    @Override
    protected Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable) {
        /* do nothing */
        return null;
    }

    /* (non-Javadoc)
     *
     * @see StatementBuilder#handleQuery(javax.persistence.Query, java.util.ArrayList)
     */
    @Override
    protected Statement handleQuery(Query query, ArrayList<Token> tokens) {
        /* do nothing, no need to handle this case */
        return null;
    }
}
