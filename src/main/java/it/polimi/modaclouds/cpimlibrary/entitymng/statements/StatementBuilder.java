package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public abstract class StatementBuilder {

    public static final boolean followCascades = true;
    protected Deque<Statement> stack = new ArrayDeque<>();
    protected List<CascadeType> cascadeTypes = new ArrayList<>();

    public StatementBuilder(List<CascadeType> cascadeTypes) {
        this.cascadeTypes = cascadeTypes;
    }

    protected abstract Deque<Statement> build(Object entity);

    protected abstract Deque<Statement> build(Query query);

    protected void handleCascadeTypes(CascadeType[] cascadeTypes, Object entity, Field field) {
        for (CascadeType cascadeType : cascadeTypes) {
            if (this.cascadeTypes.contains(cascadeType)) {
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
}
