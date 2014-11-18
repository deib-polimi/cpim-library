package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        InsertStatement statement = new InsertStatement();
        String tableName = ReflectionUtils.getTableName(entity);
        log.info("Class {} have {} as JPA table name", entity.getClass().getSimpleName(), tableName);
        statement.setTableName(tableName);

        Field[] fields = ReflectionUtils.getJdoFields(entity);
        for (Field field : fields) {
            String fieldName;
            Object fieldValue;
            if (!ReflectionUtils.isRelational(field)) {
                fieldName = ReflectionUtils.getJPAColumnName(field);
                fieldValue = ReflectionUtils.getValue(entity, field);
            } else {
                log.info("{} is a relational field", field.getName());
                if (ReflectionUtils.ownRelation(field)) {
                    log.info("{} is the owning side of the relation", field.getName());
                    fieldName = ReflectionUtils.getJoinColumnName(field);
                    fieldValue = ReflectionUtils.getJoinColumnValue(entity, fieldName, field);
                } else {
                    log.info("{} is the non-owning side of the relation, jump it", field.getName());
                    continue;
                }
            }
            log.info("{} will be {} = {}", field.getName(), fieldName, fieldValue);

            statement.addFiled(fieldName, fieldValue);
        }

        stack.addFirst(statement);
        return stack;
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
