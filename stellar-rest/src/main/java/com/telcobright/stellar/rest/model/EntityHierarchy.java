package com.telcobright.stellar.rest.model;

import java.util.List;
import java.util.Map;

/**
 * Cached entity hierarchy metadata for efficient processing
 */
public class EntityHierarchy {
    private String key; // e.g., "category-product" or "customer-salesorder-orderdetail"
    private List<EntityLevel> levels;
    private Map<String, ForeignKeyMapping> foreignKeyMappings;
    private boolean isValid;
    private long createdAt;
    private long lastUsedAt;
    private int usageCount;
    
    public static class EntityLevel {
        private String entityName;
        private String tableName;
        private String primaryKey;
        private List<String> columns;
        private Map<String, Class<?>> columnTypes;
        
        public EntityLevel(String entityName, String tableName, String primaryKey) {
            this.entityName = entityName;
            this.tableName = tableName;
            this.primaryKey = primaryKey;
        }
        
        // Getters and setters
        public String getEntityName() { return entityName; }
        public void setEntityName(String entityName) { this.entityName = entityName; }
        
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public String getPrimaryKey() { return primaryKey; }
        public void setPrimaryKey(String primaryKey) { this.primaryKey = primaryKey; }
        
        public List<String> getColumns() { return columns; }
        public void setColumns(List<String> columns) { this.columns = columns; }
        
        public Map<String, Class<?>> getColumnTypes() { return columnTypes; }
        public void setColumnTypes(Map<String, Class<?>> columnTypes) { this.columnTypes = columnTypes; }
    }
    
    public static class ForeignKeyMapping {
        private String parentEntity;
        private String childEntity;
        private String parentColumn;
        private String childColumn;
        
        public ForeignKeyMapping(String parentEntity, String childEntity, 
                                String parentColumn, String childColumn) {
            this.parentEntity = parentEntity;
            this.childEntity = childEntity;
            this.parentColumn = parentColumn;
            this.childColumn = childColumn;
        }
        
        // Getters and setters
        public String getParentEntity() { return parentEntity; }
        public void setParentEntity(String parentEntity) { this.parentEntity = parentEntity; }
        
        public String getChildEntity() { return childEntity; }
        public void setChildEntity(String childEntity) { this.childEntity = childEntity; }
        
        public String getParentColumn() { return parentColumn; }
        public void setParentColumn(String parentColumn) { this.parentColumn = parentColumn; }
        
        public String getChildColumn() { return childColumn; }
        public void setChildColumn(String childColumn) { this.childColumn = childColumn; }
    }
    
    // Constructor
    public EntityHierarchy(String key) {
        this.key = key;
        this.createdAt = System.currentTimeMillis();
        this.lastUsedAt = this.createdAt;
        this.usageCount = 0;
    }
    
    // Method to mark usage
    public void markUsed() {
        this.lastUsedAt = System.currentTimeMillis();
        this.usageCount++;
    }
    
    // Getters and setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public List<EntityLevel> getLevels() { return levels; }
    public void setLevels(List<EntityLevel> levels) { this.levels = levels; }
    
    public Map<String, ForeignKeyMapping> getForeignKeyMappings() { return foreignKeyMappings; }
    public void setForeignKeyMappings(Map<String, ForeignKeyMapping> foreignKeyMappings) { 
        this.foreignKeyMappings = foreignKeyMappings; 
    }
    
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    
    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
}