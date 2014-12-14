package it.polimi.modaclouds.cpimlibrary.entitymng;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils method that uses reflection api.
 * <p/>
 * Contains JPA specific reflection utils methods and some useful method wrapper
 * to isolate the scope of thrown exceptions.
 *
 * @author Fabio Arcidiacono.
 * @see java.lang.reflect.Field
 * @see java.lang.Class
 * @see java.lang.annotation.Annotation
 */
@Slf4j
public class ReflectionUtils {

    public static boolean isFieldAnnotatedWith(Field field, Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    public static boolean isClassAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return clazz.isAnnotationPresent(annotationType);
    }

    public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        if (isFieldAnnotatedWith(field, annotationClass)) {
            return field.getAnnotation(annotationClass);
        }
        throw new RuntimeException("Field " + field.getName() + " is not annotated with " + annotationClass.getSimpleName());
    }

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        if (isClassAnnotatedWith(clazz, annotationClass)) {
            return clazz.getAnnotation(annotationClass);
        }
        throw new RuntimeException("Class " + clazz.getSimpleName() + " is not annotated with " + annotationClass.getSimpleName());
    }

    public static Class<?> getClassInstance(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className);
        }
    }

    public static boolean isRelational(Field field) {
        return field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class);
    }

    public static boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    public static boolean ownRelation(Field field) {
        return isRelational(field) &&
                (field.isAnnotationPresent(JoinColumn.class) || field.isAnnotationPresent(JoinTable.class));
    }

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

    public static Field getField(Class<?> entityClass, String fieldName) {
        try {
            return entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot find field " + fieldName + " in class " + entityClass.getCanonicalName());
        }
    }

    public static Field[] getFields(Object entity) {
        try {
            return getJdoFields(entity);
        } catch (Exception e) {
            return entity.getClass().getDeclaredFields();
        }
    }

    private static Field[] getJdoFields(Object entity) {
        try {
            Field jdoField = entity.getClass().getDeclaredField("jdoFieldNames");
            log.info("Class {} has been enhanced with JDO", entity.getClass().getCanonicalName());
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

    public static Field[] getFieldsAnnotatedWith(Object object, Class<? extends Annotation> annotationType) {
        Field[] classFields = getFields(object);
        List<Field> fields = new ArrayList<>();
        for (Field field : classFields) {
            if (field.isAnnotationPresent(annotationType)) {
                fields.add(field);
            }
        }
        return fields.toArray(new Field[classFields.length]);
    }

    public static String getTableName(Object entity) {
        Table table = getAnnotation(entity.getClass(), Table.class);
        return table.name();
    }

    public static Field getIdField(Object entity) {
        Field[] fields = ReflectionUtils.getFieldsAnnotatedWith(entity, Id.class);
        if (fields.length > 0) {
            return fields[0]; /* just one Id per class */
        } else {
            throw new RuntimeException("Cannot find Id field for " + entity.getClass().getCanonicalName());
        }
    }

    public static String getJPAColumnName(Field field) {
        String fieldName;
        if (field.isAnnotationPresent(Column.class)) {
            log.debug("{} is annotated with @Column", field.getName());
            Column column = field.getAnnotation(Column.class);
            fieldName = column.name();
        } else {
            fieldName = field.getName();
        }
        return fieldName;
    }

    public static String getJoinColumnName(Field field) {
        JoinColumn joinColumn = getAnnotation(field, JoinColumn.class);
        return joinColumn.name();
    }

    public static Object getJoinColumnValue(Object entity, String joinColumnName, Field field) {
        Object instance = getValue(entity, field);
        log.debug("instance is {}", instance);
        Field joinColumnField;
        try {
            joinColumnField = instance.getClass().getDeclaredField(joinColumnName);
        } catch (NoSuchFieldException e) {
            /* case JPA Column name is used */
            Field[] possibleFields = getFieldsAnnotatedWith(instance, Column.class);
            joinColumnField = getJoinColumnField(joinColumnName, possibleFields);
        }
        log.debug("joinColumnField is {}", joinColumnField.getName());
        return getValue(instance, joinColumnField);
    }

    public static Field getJoinColumnField(Object entity, String joinColumnName) {
        return getJoinColumnField(joinColumnName, getFields(entity));
    }

    private static Field getJoinColumnField(String joinColumnName, Field[] possibleFields) {
        for (Field field : possibleFields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.name().equals(joinColumnName)) {
                return field;
            }
        }
        throw new RuntimeException("Field " + joinColumnName + " cannot be found.");
    }

    public static void setIdBackToEntity(Object entity, Field field, String generatedId) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(entity, generatedId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Some error occurred setting back Id", e);
        }
    }

    public static CascadeType[] getCascadeTypes(Field field) {
        if (field.isAnnotationPresent(OneToOne.class)) {
            return field.getAnnotation(OneToOne.class).cascade();
        } else if (field.isAnnotationPresent(ManyToOne.class)) {
            return field.getAnnotation(ManyToOne.class).cascade();
        } else if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).cascade();
        } else if (field.isAnnotationPresent(ManyToMany.class)) {
            return field.getAnnotation(ManyToMany.class).cascade();
        }
        throw new RuntimeException("Field " + field.getName() + " is not relational.");
    }
}
