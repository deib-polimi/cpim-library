/**
 * Copyright 2013 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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

    /**
     * Return true if {@code field} is annotated with {@code annotationType}, false otherwise.
     *
     * @param field          field to be checked
     * @param annotationType annotation to found
     *
     * @return {@code boolean}
     */
    public static boolean isFieldAnnotatedWith(Field field, Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    /**
     * Return true if {@code clazz} is annotated with {@code annotationType}, false otherwise.
     *
     * @param clazz          class to be checked
     * @param annotationType annotation to found
     *
     * @return {@code boolean}
     */
    public static boolean isClassAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return clazz.isAnnotationPresent(annotationType);
    }

    /**
     * Check if annotation {@code annotationClass} is present on the given field then returns it.
     *
     * @param field           field to be checked
     * @param annotationClass annotation to find
     * @param <T>             type of the annotation to be retrieved
     *
     * @return the annotation instance
     *
     * @throws java.lang.RuntimeException if {@code annotationClass} is not present.
     */
    public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        if (isFieldAnnotatedWith(field, annotationClass)) {
            return field.getAnnotation(annotationClass);
        }
        throw new RuntimeException("Field " + field.getName() + " is not annotated with " + annotationClass.getSimpleName());
    }

    /**
     * Check if annotation {@code annotationClass} is present on the given class then returns it.
     *
     * @param clazz           class to be checked
     * @param annotationClass annotation to find
     * @param <T>             type of the annotation to be retrieved
     *
     * @return the annotation instance
     *
     * @throws java.lang.RuntimeException if {@code annotationClass} is not present.
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        if (isClassAnnotatedWith(clazz, annotationClass)) {
            return clazz.getAnnotation(annotationClass);
        }
        throw new RuntimeException("Class " + clazz.getSimpleName() + " is not annotated with " + annotationClass.getSimpleName());
    }

    /**
     * Return an instance of the corresponding class.
     *
     * @param className name of the class to instantiate
     *
     * @return an instance of {@code className}
     *
     * @throws java.lang.RuntimeException if {@code className} cannot be found
     */
    public static Class<?> getClassInstance(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className);
        }
    }

    /**
     * Return true if field is annotated with {@link javax.persistence.Id}.
     *
     * @param field field to be checked
     *
     * @return {@code boolean}
     */
    public static boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    /**
     * Return true if field is annotated with one of {@link javax.persistence.OneToOne},
     * {@link javax.persistence.OneToMany}, {@link javax.persistence.ManyToMany} or {@link javax.persistence.ManyToOne}.
     *
     * @param field field to be checked
     *
     * @return {@code boolean}
     */
    public static boolean isRelational(Field field) {
        return field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class);
    }

    /**
     * Return true if field is the owning side of a relationship i.e. is annotated
     * with either {@link javax.persistence.JoinColumn} or {@link javax.persistence.JoinTable}.
     *
     * @param field field to be checked
     *
     * @return {@code boolean}
     */
    public static boolean ownRelation(Field field) {
        return isRelational(field) &&
                (field.isAnnotationPresent(JoinColumn.class) || field.isAnnotationPresent(JoinTable.class));
    }

    /**
     * Returns the value of a field in the given object.
     *
     * @param object object from which retrieve the value
     * @param field  interested field
     *
     * @return the field value in the given object.
     *
     * @throws java.lang.RuntimeException if problem occurs accessing {@code field}.
     */
    public static Object getFieldValue(Object object, Field field) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the requested field from the given class.
     *
     * @param entityClass class in which search for {@code fieldName}
     * @param fieldName   field to search
     *
     * @return a {@link java.lang.reflect.Field} instance.
     *
     * @throws java.lang.RuntimeException if {@code fieldName} cannot be found.
     */
    public static Field getFieldByName(Class<?> entityClass, String fieldName) {
        try {
            return entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot find field " + fieldName + " in class " + entityClass.getCanonicalName());
        }
    }

    /**
     * Tries to get field from a JDO enhanced class, if fails returns all declared fields.
     *
     * @param entity entity from which retrieve fields
     *
     * @return an array of {@link java.lang.reflect.Field}
     */
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
            // log.info("Class {} has been enhanced with JDO", entity.getClass().getCanonicalName());
            jdoField.setAccessible(true);
            String[] jdoFieldNames = (String[]) getFieldValue(entity, jdoField);
            List<Field> classFields = new ArrayList<>();
            for (String fieldName : jdoFieldNames) {
                classFields.add(entity.getClass().getDeclaredField(fieldName));
            }
            return classFields.toArray(new Field[jdoFieldNames.length]);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Impossible to get jdoFieldNames: ", e);
        }
    }

    /**
     * Returns all the fields in the given object annotated with {@code annotationType}.
     * <p/>
     * Returns an empty array if no fields are found.
     *
     * @param object         object in which search
     * @param annotationType annotation to be found
     *
     * @return an array of {@link java.lang.reflect.Field}
     */
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

    /**
     * Returns the name specified in the {@link javax.persistence.Table} annotation.
     *
     * @param entity a JPA annotated object
     *
     * @return a {@code String} representing the name
     *
     * @throws java.lang.RuntimeException if {@link javax.persistence.Table} is not present.
     */
    public static String getJPATableName(Object entity) {
        Table table = getAnnotation(entity.getClass(), Table.class);
        return table.name();
    }

    /**
     * Returns the name specified in the {@link javax.persistence.Table} annotation.
     *
     * @param clazz a JPA annotated class
     *
     * @return a {@code String} representing the name
     *
     * @throws java.lang.RuntimeException if {@link javax.persistence.Table} is not present.
     */
    public static String getJPATableName(Class<?> clazz) {
        Table table = getAnnotation(clazz, Table.class);
        return table.name();
    }

    /**
     * Returns the field  annotated with {@link javax.persistence.Id}.
     *
     * @param entity object in which search
     *
     * @return a {@link java.lang.reflect.Field}
     *
     * @throws java.lang.RuntimeException if no field is annotated as Id.
     */
    public static Field getIdField(Object entity) {
        Field[] fields = ReflectionUtils.getFieldsAnnotatedWith(entity, Id.class);
        if (fields.length > 0) {
            return fields[0]; /* just one Id per class */
        } else {
            throw new RuntimeException("Cannot find Id field for " + entity.getClass().getCanonicalName());
        }
    }

    /**
     * Return the column name specified in the {@link javax.persistence.Column} annotation.
     * <p/>
     * If no such annotation is found, returns the field name (default column name for JPA).
     *
     * @param field the field to checked
     *
     * @return a {@code String} representing the column name
     */
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

    /**
     * Returns the name of the join column specified by {@link javax.persistence.JoinColumn}.
     *
     * @param field the field to checked
     *
     * @return a {@code String} representing the join column name
     *
     * @throws java.lang.RuntimeException if {@link javax.persistence.JoinColumn} is not present.
     */
    public static String getJoinColumnName(Field field) {
        JoinColumn joinColumn = getAnnotation(field, JoinColumn.class);
        return joinColumn.name();
    }

    public static Object getJoinColumnValue(Object entity, String joinColumnName, Field field) {
        Object instance = getFieldValue(entity, field);
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
        return getFieldValue(instance, joinColumnField);
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

    /**
     * Set in {@code entity} instance, on the {@code field} field the given {@code generatedId}.
     *
     * @param entity      entity on which set the id field
     * @param field       the id field to set
     * @param generatedId the Id to be set
     *
     * @throws RuntimeException if nome error occurs while accessing {@code field}.
     */
    public static void setIdBackToEntity(Object entity, Field field, Object generatedId) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(entity, generatedId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Some error occurred setting back Id", e);
        }
    }

    /**
     * Returns the cascade types declared for the given field.
     *
     * @param field the interested field
     *
     * @return an array of {@link javax.persistence.CascadeType} declared on the field
     *
     * @throws java.lang.RuntimeException if field does not hold any relationships.
     */
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
