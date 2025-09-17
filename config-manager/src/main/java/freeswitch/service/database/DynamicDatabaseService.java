package freeswitch.service.database;

import freeswitch.config.database.DynamicRoutingDataSource;
import freeswitch.config.dynamic.core.DataLoader;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Lazy
public class DynamicDatabaseService {

    private final DataSource dataSource;
    @Autowired
    private Environment env;
    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();
    private final DataLoader dataLoader;
    @Value("${admin.db}")
    String adminDb;

    @Getter
    private final HashMap<String, String> didNumberVsDbName = new HashMap<>();
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Getter
    List<String> allValidDbs = new ArrayList<>();


    @Autowired
    public DynamicDatabaseService(@Autowired @Qualifier("mysqlDataSource") DataSource dataSource, DataLoader dataLoader) {
        this.dataSource = dataSource;
        this.dataLoader = dataLoader;
    }

    public void switchDatabase(String databaseName) {
        DynamicRoutingDataSource dynamicDataSource = (DynamicRoutingDataSource) this.dataSource;

        if (!dataSourceCache.containsKey(databaseName)) {
            DriverManagerDataSource newDataSource = new DriverManagerDataSource();
            newDataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
            newDataSource.setUrl(env.getProperty("datasource.url-base") + databaseName);
            newDataSource.setUsername(env.getProperty("spring.datasource.username"));
            newDataSource.setPassword(env.getProperty("spring.datasource.password"));

            dataSourceCache.put(databaseName, newDataSource);
            dynamicDataSource.addTargetDataSource(databaseName, newDataSource);
        }

        DynamicRoutingDataSource.setDatabase(databaseName);
    }

    @PostConstruct
    public void loadDbVsDidNumberMap() {
        this.allValidDbs = getResellerDbs();
        for(String dbName: this.allValidDbs) {
            switchDatabase(dbName);
            dataLoader.getDidNumbers().forEach(didNumber -> {
                if (this.didNumberVsDbName.containsKey(dbName)) {
                    System.out.println(this.didNumberVsDbName.get(dbName)+ " is duplicate on db-> "+dbName);
                }
                this.didNumberVsDbName.put(didNumber, dbName);
            });
        }
    }

    public List<String> getResellerDbs() {
        return jdbcTemplate.queryForList("SHOW DATABASES;", String.class).stream()
                .filter(db -> db.startsWith("res_")) // keep only tenant dbs
                .collect(Collectors.toList());
    }

}

