package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders;

import it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.ReflectionUtils;
import it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.MigrationManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.DeleteStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.UpdateStatement;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Lexer;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Token;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public abstract class StatementBuilder {

    @Setter public static boolean followCascades = false;
    private final List<CascadeType> relevantCascadeTypes;
    private Deque<Statement> stack = new ArrayDeque<>();

    public StatementBuilder(List<CascadeType> relevantCascadeTypes) {
        this.relevantCascadeTypes = relevantCascadeTypes;
    }

    public boolean isFollowingCascades() {
        return followCascades;
    }

    protected void addToStack(Statement statement) {
        stack.addFirst(statement);
    }

    /*---------------------------------------------------------------------------------*/
    /*----------------------------- BUILD FROM OBJECT ---------------------------------*/
    /*---------------------------------------------------------------------------------*/

    /**
     * Main abstract algorithm that build statements from object. Follows template pattern.
     * Abstract methods are implemented in sub classes, is possible to modify the standard behavior
     * of the algorithm overriding the hook methods (the protected ones).
     *
     * @param entity the object from which build statements
     *
     * @return a {@link java.util.Deque} used as stack containing the statements build from the given entity
     */
    public Deque<Statement> build(Object entity) {
        Statement statement = initStatement();
        setTableName(statement, entity);

        Field[] fields = getFields(entity);
        for (Field field : fields) {
            if (ReflectionUtils.isRelational(field)) {
                log.debug("{} is a relational field", field.getName());
                if (ReflectionUtils.ownRelation(field)) {
                    log.debug("{} is the owning side of the relation", field.getName());
                    if (followCascades) {
                        handleCascade(entity, field);
                    } else {
                        log.info("Ignore cascade on field {}", field.getName());
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

    protected abstract Statement generateJoinTableStatement(Object entity, Object element, JoinTable joinTable);

    protected abstract Statement generateInverseJoinTableStatement(Object entity, JoinTable joinTable);

    protected abstract void onIdField(Statement statement, Object entity, Field idFiled);

    protected abstract void onFiled(Statement statement, Object entity, Field field);

    protected abstract void onRelationalField(Statement statement, Object entity, Field field);

    /**
     * Look for table name inside the entity and modifies the injected statements accordingly.
     *
     * @param statement injected statement to modify
     * @param entity    entity to be parsed
     */
    protected void setTableName(Statement statement, Object entity) {
        String tableName = ReflectionUtils.getTableName(entity);
        log.debug("Class {} have {} as JPA table name", entity.getClass().getSimpleName(), tableName);
        statement.setTable(tableName);
    }

    /**
     * Hook to the way the fields are retrieved from the entity.
     *
     * @param entity entity to be parsed
     *
     * @return an array of entity fields
     */
    protected Field[] getFields(Object entity) {
        return ReflectionUtils.getFields(entity);
    }

    /**
     * Gets cascade types declared on field and if necessary call a statement build on related entities.
     *
     * @param entity entity to be parsed
     * @param field  a relational field
     */
    protected void handleCascade(Object entity, Field field) {
        CascadeType[] declaredCascadeTypes = ReflectionUtils.getCascadeTypes(field);
        for (CascadeType cascadeType : declaredCascadeTypes) {
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

    /**
     * From the owning side of the relationship, iterate through collection of related entities
     * and for each one calls {@link #generateJoinTableStatement(Object, Object, javax.persistence.JoinTable)}.
     *
     * @param entity entity to be parsed
     * @param field  the field owning the {@link javax.persistence.ManyToMany} relationship
     */
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

    /**
     * From the non-owning side of the relationship, prepare data to call
     * {@link #generateInverseJoinTableStatement(Object, javax.persistence.JoinTable)}.
     *
     * @param entity entity to be parsed
     * @param field  the field owning the inverse side of the {@link javax.persistence.ManyToMany} relationship
     */
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

    /**
     * Add the field to statement with as value the Id of the related entity,
     * taking into account the JPA name associated to the field.
     *
     * @param statement injected statement to modify
     * @param entity    entity to be parsed
     * @param field     relational field to add to statement
     */
    protected void addRelationalFiled(Statement statement, Object entity, Field field) {
        String fieldName = ReflectionUtils.getJoinColumnName(field);
        Object fieldValue = ReflectionUtils.getJoinColumnValue(entity, fieldName, field);
        log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
        statement.addField(fieldName, fieldValue);
    }

    /**
     * Add the field to statement taking into account the JPA name associated to the field.
     *
     * @param statement injected statement to modify
     * @param entity    entity to be parsed
     * @param field     field to add to statement
     */
    protected void addField(Statement statement, Object entity, Field field) {
        String fieldName = ReflectionUtils.getJPAColumnName(field);
        Object fieldValue = ReflectionUtils.getValue(entity, field);
        log.debug("{} will be {} = {}", field.getName(), fieldName, fieldValue);
        statement.addField(fieldName, fieldValue);
    }

    /*---------------------------------------------------------------------------------*/
    /*----------------------------- BUILD FROM QUERY ----------------------------------*/
    /*---------------------------------------------------------------------------------*/

    /*
     * no need to update join tables, update or delete by query is not possible through JPA.
     *
     * no need to handle cascade type for now since currently Kundera does not support
     * dot notations in queries so is not possible to update through a query another entity
     * beside the one explicitly stated in the query
     */
    public Deque<Statement> build(Query query) {

        String qlString;
        if (query instanceof CloudQuery) {
            qlString = ((CloudQuery) query).getQlString();
        } else if (query instanceof TypedCloudQuery) {
            qlString = ((TypedCloudQuery) query).getQlString();
        } else {
            throw new RuntimeException("Query has not been wrapped by CPIM");
        }
        log.info(qlString);
        Statement statement;
        ArrayList<Token> tokens = Lexer.lex(qlString);

        Token first = tokens.get(0);
        if (first.type.equals(TokenType.UPDATE)) {
            statement = handleUpdate(query, tokens);
        } else if (first.type.equals(TokenType.DELETE)) {
            statement = handleDelete(query, tokens);
        } else {
            throw new RuntimeException("Query is neither UPDATE nor DELETE");
        }

        addToStack(statement);
        return stack;
    }

    protected Statement handleDelete(Query query, ArrayList<Token> tokens) {
        Iterator<Token> itr = tokens.iterator();
        String objectParam = "";
        Statement statement = new DeleteStatement();
        while (itr.hasNext()) {
            Token current = itr.next();
            switch (current.type) {
                case DELETE:
                case WHERE:
                case WHITESPACE:
                    /* fall through */
                    break;
                case FROM:
                    String tableName = nextTokenOfType(TokenType.STRING, itr);
                    statement.setTable(tableName);
                    objectParam = nextTokenOfType(TokenType.STRING, itr);
                    break;
                case COLUMN:
                    String name = current.data.replaceAll(objectParam + ".", "");
                    String column = getJPAColumnName(name, statement.getTable());
                    String operator = nextTokenOfType(TokenType.COMPAREOP, itr);
                    String param = nextTokenOfType(TokenType.PARAM, itr).replaceFirst(":", "");
                    Object value = query.getParameterValue(query.getParameter(param));
                    statement.addCondition(column, operator, value);
                    break;
                case LOGICOP:
                    statement.addCondition(current.data);
            }
        }
        return statement;
    }

    protected Statement handleUpdate(Query query, ArrayList<Token> tokens) {
        Iterator<Token> itr = tokens.iterator();
        String objectParam = "";
        boolean wherePart = false;
        Statement statement = new UpdateStatement();
        while (itr.hasNext()) {
            Token current = itr.next();
            switch (current.type) {
                case SET:
                case WHITESPACE:
                    /* fall through */
                    break;
                case UPDATE:
                    String tableName = nextTokenOfType(TokenType.STRING, itr);
                    statement.setTable(tableName);
                    objectParam = nextTokenOfType(TokenType.STRING, itr);
                    break;
                case WHERE:
                    wherePart = true;
                    break;
                case COLUMN:
                    String name = current.data.replaceAll(objectParam + ".", "");
                    String column = getJPAColumnName(name, statement.getTable());
                    String operator = nextTokenOfType(TokenType.COMPAREOP, itr);
                    String param = nextTokenOfType(TokenType.PARAM, itr).replaceAll(":|,", "");
                    Object value = query.getParameterValue(query.getParameter(param));
                    if (wherePart) {
                        statement.addCondition(column, operator, value);
                    } else {
                        /* is in the SET part */
                        statement.addField(column, value);
                    }
                    break;
                case LOGICOP:
                    statement.addCondition(current.data);
            }
        }
        return statement;
    }

    protected String getJPAColumnName(String name, String tableName) {
        String fullClassName = MigrationManager.getInstance().getMappedClass(tableName);
        if (fullClassName == null) {
            throw new RuntimeException(tableName + " is unknown");
        }

        Class<?> clazz = ReflectionUtils.getClassInstance(fullClassName);
        Field field = ReflectionUtils.getField(clazz, name);
        return ReflectionUtils.getJPAColumnName(field);
    }

    protected String nextTokenOfType(TokenType type, Iterator<Token> itr) {
        Token current = itr.next();
        if (current.type.equals(type)) {
            return current.data;
        }
        return nextTokenOfType(type, itr);
    }
}
