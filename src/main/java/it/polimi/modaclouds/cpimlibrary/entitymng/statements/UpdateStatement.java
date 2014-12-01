package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
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
 * Expose methods to build UPDATE statements.
 *
 * @author Fabio Arcidiacono.
 * @see Statement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateStatement extends Statement {

    private String tableName;
    private List<Filter> sets = new ArrayList<>();
    private List<Filter> filters = new ArrayList<>();
    private static StatementBuilder builder = new UpdateBuilder();

    public static Deque<Statement> build(Object entity) {
        return builder.build(entity);
    }

    public static Deque<Statement> build(Query query) {
        return builder.build(query);
    }

    public void addSet(String name, String operator, Object value) {
        this.sets.add(new Filter(name, operator, value));
    }

    public void addCondition(String name, String operator, Object value) {
        this.filters.add(new Filter(name, operator, value));
    }

    @Override
    public String toString() {
        String setList = "";
        Iterator entries = this.sets.iterator();
        while (entries.hasNext()) {
            Filter filter = (Filter) entries.next();
            setList += filter.toString();
            if (entries.hasNext()) {
                setList += ", ";
            }
        }
        if (filters.isEmpty()) {
            return "UPDATE " + this.tableName + " SET  " + setList;
        } else {
            String filterList = "";
            entries = this.filters.iterator();
            while (entries.hasNext()) {
                Filter filter = (Filter) entries.next();
                filterList += filter.toString();
                if (entries.hasNext()) {
                    filterList += ", ";
                }
            }
            return "UPDATE " + this.tableName + " SET  " + setList + " WHERE " + filterList;
        }
    }
}

/**
 * Expose methods to build UPDATE statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.StatementBuilder
 */
@Slf4j
class UpdateBuilder extends StatementBuilder {

    public UpdateBuilder() {
        super(Arrays.asList(CascadeType.ALL, CascadeType.MERGE));
    }

    @Override
    protected Deque<Statement> build(Object entity) {
        UpdateStatement statement = new UpdateStatement();
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
                        log.debug("{} holds a ManyToMany relationship, generate updates for JoinTable", field.getName());
                        addJoinTableUpdates(entity, field, stack);
                    } else {
                        fieldName = ReflectionUtils.getJoinColumnName(field);
                        fieldValue = ReflectionUtils.getJoinColumnValue(entity, fieldName, field);
                        log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
                        statement.addSet(fieldName, "=", fieldValue);
                    }
                } else {
                    log.debug("{} is the non-owning side of the relation, ignore it", field.getName());
                }
            } else {
                if (ReflectionUtils.isId(field)) {
                    Field idFiled = ReflectionUtils.getIdField(entity);
                    String jpaColumnName = ReflectionUtils.getJPAColumnName(idFiled);
                    Object idValue = ReflectionUtils.getValue(entity, idFiled);
                    log.debug("id filed is {}, will be {} = {}", idFiled.getName(), jpaColumnName, idValue);
                    statement.addCondition(jpaColumnName, "=", idValue);
                } else {
                    fieldName = ReflectionUtils.getJPAColumnName(field);
                    fieldValue = ReflectionUtils.getValue(entity, field);
                    log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
                    statement.addSet(fieldName, "=", fieldValue);
                }
            }
        }

        log.info(statement.toString());
        stack.addFirst(statement);
        return stack;
    }

    private void addJoinTableUpdates(Object entity, Field field, Deque<Statement> stack) {
        JoinTable joinTable = ReflectionUtils.getAnnotation(field, JoinTable.class);
        String joinTableName = joinTable.name();
        String joinColumnName = joinTable.joinColumns()[0].name();
        String inverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();

        Field joinColumnField = ReflectionUtils.getJoinColumnField(entity, joinColumnName);
        Object joinColumnValue = ReflectionUtils.getValue(entity, joinColumnField);

        Collection collection = (Collection) ReflectionUtils.getValue(entity, field);
        for (Object element : collection) {
            UpdateStatement statement = new UpdateStatement();
            statement.setTableName(joinTableName);
            statement.addSet(joinColumnName, "=", joinColumnValue);

            Field inverseJoinColumnField = ReflectionUtils.getJoinColumnField(element, inverseJoinColumnName);
            Object inverseJoinColumnValue = ReflectionUtils.getValue(element, inverseJoinColumnField);
            statement.addSet(inverseJoinColumnName, "=", inverseJoinColumnValue);

            log.debug("joinTable {}, joinColumn {} = {}, inverseJoinColumn {} = {}", joinTableName, joinColumnName, joinColumnValue, inverseJoinColumnName, inverseJoinColumnValue);
            stack.addLast(statement);
        }
    }

    @Override
    protected Deque<Statement> build(Query query) {
        // TODO Auto-generated method stub
        return null;
    }
}

