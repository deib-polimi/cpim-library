package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class ReflectionUtils {

    public static Object getValue(Object object, Field field) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field[] getJdoFields(Object entity) {
        try {
            Field jdoField = entity.getClass().getDeclaredField("jdoFieldNames");
            jdoField.setAccessible(true);
            String[] jdoFieldNames = (String[]) getValue(entity, jdoField);
            List<Field> classFields = new ArrayList<>();
            for (String fieldName : jdoFieldNames) {
                classFields.add(entity.getClass().getDeclaredField(fieldName));
            }
            return classFields.toArray(new Field[jdoFieldNames.length]);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Impossible to get jdoFieldNames: ", e);
        }
    }

    public static String getTableName(Object entity) {
        if (entity.getClass().isAnnotationPresent(Table.class)) {
            Table table = entity.getClass().getAnnotation(Table.class);
            return table.name();
        }
        throw new RuntimeException("Class " + entity.getClass() + " must be annotated with @Table");
    }

    public static String getJPAColumnName(Field field) {
        String fieldName;
        if (field.isAnnotationPresent(Column.class)) {
            log.info("{} is annotated with @Column", field.getName());
            Column column = field.getAnnotation(Column.class);
            fieldName = column.name();
        } else {
            fieldName = field.getName();
        }
        return fieldName;
    }

    public static boolean ownRelation(Field field) {
        return isRelational(field) && field.isAnnotationPresent(JoinColumn.class);
    }

    public static boolean isRelational(Field field) {
        return field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class);
    }

    public static String getJoinColumnName(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            return joinColumn.name();
        }
        throw new RuntimeException("Field " + field.getName() + " must be annotated with @JoinColumn");
    }

    public static Object getJoinColumnValue(Object entity, String joinColumnName, Field field) {
        Object instance = getValue(entity, field);
        log.info("instance is {}", instance);
        Field joinColumnField;
        try {
            joinColumnField = instance.getClass().getDeclaredField(joinColumnName);
        } catch (NoSuchFieldException e) {
            /* case JPA Column name is used */
            Field[] possibleFields = getFieldsAnnotatedWith(instance, Column.class);
            joinColumnField = getJoinColumnField(joinColumnName, possibleFields);
        }
        log.info("joinColumnField is {}", joinColumnField.getName());
        return getValue(instance, joinColumnField);
    }

    public static Field[] getFieldsAnnotatedWith(Object object, Class<? extends Annotation> annotationType) {
        Field[] classFields = getJdoFields(object);
        List<Field> fields = new ArrayList<>();
        for (Field field : classFields) {
            if (field.isAnnotationPresent(annotationType)) {
                fields.add(field);
            }
        }
        return fields.toArray(new Field[classFields.length]);
    }

    private static Field getJoinColumnField(String joinColumnName, Field[] possibleFields) {
        for (Field field : possibleFields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (column.name().equals(joinColumnName)) {
                    return field;
                }
            }
        }
        throw new RuntimeException("Field " + joinColumnName + " cannot be found.");
    }
}
