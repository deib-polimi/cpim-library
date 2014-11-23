package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Represents an INSERT statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InsertStatement extends Statement {

    private String tableName;
    private Map<String, Object> fields = new HashMap<>();
    private static StatementBuilder builder = new InsertBuilder();

    public static Deque<Statement> build(Object entity) {
        return builder.build(entity);
    }

    public void addFiled(String name, Object value) {
        this.fields.put(name, value);
    }

    @Override
    public String toString() {
        String fieldList = "";
        String fieldValueList = "";
        Iterator entries = this.fields.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            fieldList += entry.getKey();
            fieldValueList += entry.getValue();
            if (entries.hasNext()) {
                fieldList += ", ";
                fieldValueList += ", ";
            }
        }
        return "INSERT INTO " + this.tableName + " (" + fieldList + ") VALUES (" + fieldValueList + ")";
    }
}

/**
 * Expose methods to build INSERT statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.StatementBuilder
 */
@Slf4j
class InsertBuilder extends StatementBuilder {

    @Override
    protected List<CascadeType> setCascadeTypes() {
        return Arrays.asList(CascadeType.ALL, CascadeType.PERSIST);
    }

    @Override
    protected Deque<Statement> build(Object entity) {
        InsertStatement statement = new InsertStatement();
        String tableName = ReflectionUtils.getTableName(entity);
        log.debug("Class {} have {} as JPA table name", entity.getClass().getSimpleName(), tableName);
        statement.setTableName(tableName);

        Field[] fields = ReflectionUtils.getFields(entity);
        for (Field field : fields) {
            String fieldName;
            Object fieldValue;
            if (ReflectionUtils.isRelational(field)) {
                log.debug("{} is a relational field", field.getName());
                if (ReflectionUtils.ownRelation(field)) {
                    log.debug("{} is the owning side of the relation", field.getName());
                    if (followCascades) {
                        CascadeType[] cascadeTypes = ReflectionUtils.getCascadeTypes(field);
                        handleCascadeTypes(cascadeTypes, entity, field);
                    } else {
                        log.warn("Ignore cascades");
                    }
                    if (ReflectionUtils.isFieldAnnotatedWith(field, ManyToMany.class)) {
                        log.debug("{} holds a ManyToMany relationship, generate inserts for JoinTable", field.getName());
                        addJoinTableInserts(entity, field, stack);
                        continue;
                    } else {
                        fieldName = ReflectionUtils.getJoinColumnName(field);
                        fieldValue = ReflectionUtils.getJoinColumnValue(entity, fieldName, field);
                    }
                } else {
                    log.debug("{} is the non-owning side of the relation, ignore it", field.getName());
                    continue;
                }
            } else {
                fieldName = ReflectionUtils.getJPAColumnName(field);
                fieldValue = ReflectionUtils.getValue(entity, field);
            }
            log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
            statement.addFiled(fieldName, fieldValue);
        }

        log.info(statement.toString());
        stack.addFirst(statement);
        return stack;
    }

    private void addJoinTableInserts(Object entity, Field field, Deque<Statement> stack) {
        JoinTable joinTable = ReflectionUtils.getAnnotation(field, JoinTable.class);
        String joinTableName = joinTable.name();
        String joinColumnName = joinTable.joinColumns()[0].name();
        String inverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();

        Field joinColumnField = ReflectionUtils.getJoinColumnField(entity, joinColumnName);
        Object joinColumnValue = ReflectionUtils.getValue(entity, joinColumnField);

        Collection collection = (Collection) ReflectionUtils.getValue(entity, field);
        for (Object element : collection) {
            InsertStatement statement = new InsertStatement();
            statement.setTableName(joinTableName);
            statement.addFiled(joinColumnName, joinColumnValue);

            Field inverseJoinColumnField = ReflectionUtils.getJoinColumnField(element, inverseJoinColumnName);
            Object inverseJoinColumnValue = ReflectionUtils.getValue(element, inverseJoinColumnField);
            statement.addFiled(inverseJoinColumnName, inverseJoinColumnValue);

            log.debug("joinTable {}, joinColumn {} = {}, inverseJoinColumn {} = {}", joinTableName, joinColumnName, joinColumnValue, inverseJoinColumnName, inverseJoinColumnValue);
            stack.addLast(statement);
        }
    }

    @Override
    protected Deque<Statement> build(Query query) {
        throw new UnsupportedOperationException("INSERT statements cannot be build from query, " +
                "JPA does not have a specification for INSERT statements");
    }
}
