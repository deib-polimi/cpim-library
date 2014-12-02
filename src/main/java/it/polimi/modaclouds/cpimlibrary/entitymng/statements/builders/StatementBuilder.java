package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public abstract class StatementBuilder {

    public static final boolean FOLLOW_CASCADES = false;
    private final List<CascadeType> relevantCascadeTypes;
    private Deque<Statement> stack = new ArrayDeque<>();

    public StatementBuilder(List<CascadeType> relevantCascadeTypes) {
        this.relevantCascadeTypes = relevantCascadeTypes;
    }

    public Deque<Statement> build(Object entity) {
        Statement statement = initStatement();
        setTableName(statement, entity);

        Field[] fields = getFields(entity);
        for (Field field : fields) {
            if (ReflectionUtils.isRelational(field)) {
                log.debug("{} is a relational field", field.getName());
                if (ReflectionUtils.ownRelation(field)) {
                    log.debug("{} is the owning side of the relation", field.getName());
                    if (FOLLOW_CASCADES) {
                        CascadeType[] cascadeTypes = ReflectionUtils.getCascadeTypes(field);
                        handleCascade(cascadeTypes, entity, field);
                    } else {
                        log.warn("Ignore cascade on field {}", field.getName());
                    }
                    if (ReflectionUtils.isFieldAnnotatedWith(field, ManyToMany.class)) {
                        log.debug("{} holds a ManyToMany relationship, handle JoinTable", field.getName());
                        handleJoinTable(entity, field);
                    } else {
                        onRelationalField(statement, entity, field);
                    }
                } else {
                    log.debug("{} is the non-owning side of the relation, ignore it", field.getName());
                    if (ReflectionUtils.isFieldAnnotatedWith(field, ManyToMany.class)) {
                        log.debug("{} holds a inverse ManyToMany relationship, handle JoinTable", field.getName());
                        handleInverseJoinTable(entity, field);
                    }
                }
            } else if (ReflectionUtils.isId(field)) {
                onIdField(statement, entity, field);
            } else {
                onFiled(statement, entity, field);
            }
        }

        addToStack(statement);
        return stack;
    }

    protected abstract Statement initStatement();

    protected void setTableName(Statement statement, Object entity) {
        String tableName = ReflectionUtils.getTableName(entity);
        log.debug("Class {} have {} as JPA table name", entity.getClass().getSimpleName(), tableName);
        statement.setTable(tableName);
    }

    protected Field[] getFields(Object entity) {
        return ReflectionUtils.getFields(entity);
    }

    protected abstract void onRelationalField(Statement statement, Object entity, Field field);

    protected void handleCascade(CascadeType[] cascadeTypes, Object entity, Field field) {
        for (CascadeType cascadeType : cascadeTypes) {
            if (this.relevantCascadeTypes.contains(cascadeType)) {
                Object cascadeEntity = ReflectionUtils.getValue(entity, field);
                if (cascadeEntity instanceof Collection) {
                    for (Object cascade : (Collection) cascadeEntity) {
                        log.warn("Cascade operation on collection field {} with value {}", field.getName(), cascade);
                        build(cascade);
                    }
                } else {
                    log.warn("Cascade operation on field {} with value {}", field.getName(), cascadeEntity);
                    build(cascadeEntity);
                }
            }
        }
    }

    protected void handleJoinTable(Object entity, Field field) {
        JoinTable joinTable = ReflectionUtils.getAnnotation(field, JoinTable.class);

        Collection collection = (Collection) ReflectionUtils.getValue(entity, field);
        for (Object element : collection) {
            Statement statement = generateJoinTableStatement(entity, element, joinTable);
            if (statement != null) {
                log.info("joinTableStatement: {}", statement.toString());
                stack.addLast(statement);
            }
        }
    }

    protected void handleInverseJoinTable(Object entity, Field field) {
        ManyToMany mtm = ReflectionUtils.getAnnotation(field, ManyToMany.class);
        String mappedBy = mtm.mappedBy();
        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
        Class<?> ownerClass = (Class<?>) collectionType.getActualTypeArguments()[0];
        log.debug("{} is mapped by {} in class {}", field.getName(), mappedBy, ownerClass.getCanonicalName());

        Field ownerField = ReflectionUtils.getField(ownerClass, mappedBy);
        JoinTable joinTable = ReflectionUtils.getAnnotation(ownerField, JoinTable.class);
        Statement statement = generateInverseJoinTableStatement(entity, joinTable);
        if (statement != null) {
            log.info("joinTableStatement: {}", statement.toString());
            stack.addLast(statement);
        }
    }

    protected abstract Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable);

    protected abstract Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable);

    protected abstract void onIdField(Statement statement, Object entity, Field idFiled);

    protected abstract void onFiled(Statement statement, Object entity, Field field);

    protected void addRelationalFiled(Statement statement, Object entity, Field field) {
        String fieldName = ReflectionUtils.getJoinColumnName(field);
        Object fieldValue = ReflectionUtils.getJoinColumnValue(entity, fieldName, field);
        log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
        statement.addField(fieldName, fieldValue);
    }

    protected void addFiled(Statement statement, Object entity, Field field) {
        String fieldName = ReflectionUtils.getJPAColumnName(field);
        Object fieldValue = ReflectionUtils.getValue(entity, field);
        log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
        statement.addField(fieldName, fieldValue);
    }

    protected void addToStack(Statement statement) {
        log.info(statement.toString());
        stack.addFirst(statement);
    }

    public Deque<Statement> build(Query query) {
        // TODO
        throw new NotImplementedException();
    }
}
