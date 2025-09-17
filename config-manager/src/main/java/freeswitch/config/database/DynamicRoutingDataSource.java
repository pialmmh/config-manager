package freeswitch.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> currentDatabase = new InheritableThreadLocal<>();
    private static final Map<Object, Object> globalDataSources = new ConcurrentHashMap<>();

    @Override
    public Object determineCurrentLookupKey() {
        return currentDatabase.get();
    }

    public static void setDatabase(String database) {
        currentDatabase.set(database);
    }

    public static void clearDatabase() {
        currentDatabase.remove();
    }

    public void addTargetDataSource(String dbName, DataSource dataSource) {
        globalDataSources.put(dbName, dataSource);
        super.setTargetDataSources(globalDataSources);
        super.afterPropertiesSet(); // Refresh the data source map
    }

    public boolean hasDataSource(String dbName) {
        return globalDataSources.containsKey(dbName);
    }
}
