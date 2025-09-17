package freeswitch.config.database;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = {"freeswitch.repository.mysqlrepository"},
        entityManagerFactoryRef = "mysqlEntityManager",
        transactionManagerRef = "mysqlTransactionManager"
)
public class MySQLConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();

        // Default MySQL database (used at startup)
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
        defaultDataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
        defaultDataSource.setUrl(env.getProperty("spring.datasource.url"));
        defaultDataSource.setUsername(env.getProperty("spring.datasource.username"));
        defaultDataSource.setPassword(env.getProperty("spring.datasource.password"));

        targetDataSources.put("default", defaultDataSource);
        dynamicRoutingDataSource.setTargetDataSources(targetDataSources);
        dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);

        return dynamicRoutingDataSource;
    }

    @Primary
    @Bean(name = "mysqlEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(
            @Autowired @Qualifier("mysqlDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.telcobright.rtc.domainmodel.mysqlentity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", false);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "mysqlTransactionManager")
    public JpaTransactionManager transactionManager(
            @Autowired @Qualifier("mysqlEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }
}

