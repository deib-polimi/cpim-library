package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class UpdateBuilder extends StatementBuilder {

    public UpdateBuilder() {
        super(Arrays.asList(CascadeType.ALL, CascadeType.MERGE));
    }

    @Override
    protected Statement initStatement() {
        return new UpdateStatement();
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
        String jpaColumnName = ReflectionUtils.getJPAColumnName(idFiled);
        Object idValue = ReflectionUtils.getValue(entity, idFiled);
        log.debug("id filed is {}, will be {} = {}", idFiled.getName(), jpaColumnName, idValue);
        statement.addCondition(jpaColumnName, CompareOperator.EQUAL, idValue);
    }

    @Override
    protected Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable) {
        /* no need to update joinTable */
        return null;
    }

    @Override
    protected Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable) {
        /* no need to update joinTable */
        return null;
    }
}
