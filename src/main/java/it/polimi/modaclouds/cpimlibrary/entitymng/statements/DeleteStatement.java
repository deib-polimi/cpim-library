package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Represents an DELETE statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeleteStatement extends Statement {

    private String tableName;
    private List<Filter> filters = new ArrayList<>();
    private static StatementBuilder builder = new DeleteBuilder();

    public static Deque<Statement> build(Object entity) {
        return builder.build(entity);
    }

    public static Deque<Statement> build(Query query) {
        return builder.build(query);
    }

    public void addCondition(String name, String operator, Object value) {
        this.filters.add(new Filter(name, operator, value));
    }

    @Override
    public String toString() {
        if (filters.isEmpty()) {
            return "DELETE FROM " + this.tableName;
        } else {
            String filterList = "";
            Iterator entries = this.filters.iterator();
            while (entries.hasNext()) {
                Filter filter = (Filter) entries.next();
                filterList += filter.toString();
                if (entries.hasNext()) {
                    filterList += ", ";
                }
            }
            return "DELETE FROM " + this.tableName + " WHERE " + filterList;
        }
    }

    @Data
    @AllArgsConstructor
    private class Filter {
        private String name;
        private String operator;
        private Object value;

        @Override
        public String toString() {
            return this.name + " " + this.operator + " " + this.value;
        }
    }
}

/**
 * Expose methods to build DELETE statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.StatementBuilder
 */
@Slf4j
class DeleteBuilder extends StatementBuilder {

    @Override
    protected List<CascadeType> setCascadeTypes() {
        return Arrays.asList(CascadeType.ALL, CascadeType.REMOVE);
    }

    @Override
    protected Deque<Statement> build(Object entity) {
        DeleteStatement statement = new DeleteStatement();
        String tableName = ReflectionUtils.getTableName(entity);
        log.debug("Class {} have {} as JPA table name", entity.getClass().getSimpleName(), tableName);
        statement.setTableName(tableName);

        Field[] fields = ReflectionUtils.getFields(entity);
        for (Field field : fields) {
            if (ReflectionUtils.isId(field)) {
                Field idFiled = ReflectionUtils.getIdField(entity);
                String jpaColumnName = ReflectionUtils.getJPAColumnName(idFiled);
                Object idValue = ReflectionUtils.getValue(entity, idFiled);
                log.debug("id filed is {}, will be {} = {}", idFiled.getName(), jpaColumnName, idValue);
                statement.addCondition(jpaColumnName, "=", idValue);
            } else if (ReflectionUtils.isRelational(field)) {
                if (ReflectionUtils.ownRelation(field)) {
                    if (followCascades) {
                        CascadeType[] cascadeTypes = ReflectionUtils.getCascadeTypes(field);
                        handleCascadeTypes(cascadeTypes, entity, field);
                    } else {
                        log.warn("Ignore cascades");
                    }
                }
            }
        }

        log.info(statement.toString());
        stack.addFirst(statement);
        return stack;
    }

    @Override
    protected Deque<Statement> build(Query query) {
        // TODO Auto-generated method stub
        return null;
    }
}
