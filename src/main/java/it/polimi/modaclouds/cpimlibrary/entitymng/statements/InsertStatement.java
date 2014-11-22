package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Expose methods to build INSERT statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InsertStatement extends Statement {

    private String tableName;
    private Map<String, Object> fields = new HashMap<>();

    public void addFiled(String name, Object value) {
        this.fields.put(name, value);
    }

    public static Deque<Statement> build(Object entity) {
        Deque<Statement> stack = new ArrayDeque<>();
        return build(entity, stack);
    }

    private static Deque<Statement> build(Object entity, Deque<Statement> stack) {
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
                        handleCascadeTypes(cascadeTypes, entity, field, stack);
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

    private static void handleCascadeTypes(CascadeType[] cascadeTypes, Object entity, Field field, Deque<Statement> stack) {
        for (CascadeType cascadeType : cascadeTypes) {
            if (cascadeType.equals(CascadeType.ALL) || cascadeType.equals(CascadeType.PERSIST)) {
                Object cascadeEntity = ReflectionUtils.getValue(entity, field);
                log.warn("Cascade insert on field {} with value {}", field.getName(), cascadeEntity);
                build(cascadeEntity, stack);
            }
        }
    }

    private static void addJoinTableInserts(Object entity, Field field, Deque<Statement> stack) {
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
