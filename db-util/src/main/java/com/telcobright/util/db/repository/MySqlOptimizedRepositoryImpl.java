package com.telcobright.util.db.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of MySqlOptimizedRepository with MySQL extended INSERT support.
 */
public class MySqlOptimizedRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements MySqlOptimizedRepository<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> domainClass;
    private final String tableName;
    private final List<FieldInfo> insertableFields;
    private final String insertSqlTemplate;

    public MySqlOptimizedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                        EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.domainClass = entityInformation.getJavaType();
        this.tableName = extractTableName();
        this.insertableFields = extractInsertableFields();
        this.insertSqlTemplate = buildInsertSqlTemplate();
    }

    @Override
    @Transactional
    public int insertExtendedToMysql(List<T> entities) {
        return insertExtendedToMysql(entities, 1000);
    }

    @Override
    @Transactional
    public int insertExtendedToMysql(List<T> entities, int batchSize) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        int totalInserted = 0;
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entities.size());
            List<T> batch = entities.subList(i, end);
            totalInserted += insertBatch(batch);
        }

        return totalInserted;
    }

    private int insertBatch(List<T> batch) {
        if (batch.isEmpty()) {
            return 0;
        }

        // Build MySQL extended INSERT syntax
        String sql = buildBatchInsertSql(batch.size());

        try {
            // Get connection from EntityManager
            Connection conn = entityManager.unwrap(Connection.class);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set parameters for all entities
                int paramIndex = 1;
                for (T entity : batch) {
                    for (FieldInfo field : insertableFields) {
                        Object value = field.getValue(entity);
                        pstmt.setObject(paramIndex++, value);
                    }
                }

                return pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Fall back to standard JPA if MySQL extended insert fails
            return standardJpaBatchInsert(batch);
        }
    }

    private int standardJpaBatchInsert(List<T> entities) {
        // Fallback to standard JPA saveAll
        List<T> saved = saveAll(entities);
        entityManager.flush();
        return saved.size();
    }

    private String buildBatchInsertSql(int batchSize) {
        // Build VALUES placeholders
        String valuePlaceholder = "(" +
            insertableFields.stream()
                .map(f -> "?")
                .collect(Collectors.joining(", ")) + ")";

        String values = Collections.nCopies(batchSize, valuePlaceholder)
            .stream()
            .collect(Collectors.joining(", "));

        return insertSqlTemplate + " VALUES " + values;
    }

    private String buildInsertSqlTemplate() {
        String columns = insertableFields.stream()
            .map(f -> f.columnName)
            .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s)", tableName, columns);
    }

    private String extractTableName() {
        Table tableAnnotation = domainClass.getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }

        // Convert class name to snake_case
        String className = domainClass.getSimpleName();
        return camelToSnake(className);
    }

    private List<FieldInfo> extractInsertableFields() {
        List<FieldInfo> fields = new ArrayList<>();

        for (Field field : getAllFields(domainClass)) {
            field.setAccessible(true);

            // Skip @Id with @GeneratedValue
            if (field.isAnnotationPresent(Id.class) &&
                field.isAnnotationPresent(GeneratedValue.class)) {
                continue;
            }

            // Get column name
            String columnName = getColumnName(field);

            // Check if insertable
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null && !columnAnnotation.insertable()) {
                continue;
            }

            fields.add(new FieldInfo(field, columnName));
        }

        return fields;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private String getColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name();
        }
        return camelToSnake(field.getName());
    }

    private String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private static class FieldInfo {
        final Field field;
        final String columnName;

        FieldInfo(Field field, String columnName) {
            this.field = field;
            this.columnName = columnName;
        }

        Object getValue(Object entity) {
            try {
                return field.get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to get field value: " + field.getName(), e);
            }
        }
    }
}