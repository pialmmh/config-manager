//package freeswitch.config.database;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//@Configuration
//@EnableJpaRepositories(
//        basePackages = "freeswitch.repository.postgresrepository",
//        entityManagerFactoryRef = "postgresEntityManager",
//        transactionManagerRef = "postgresTransactionManager"
//)
//public class PostgresConfig {
//    @Autowired
//    private Environment env;
//
//    @Bean(name = "postgresDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.postgres")
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.postgres.driver-class-name")));
//        dataSource.setUrl(env.getProperty("spring.datasource.postgres.url"));
//        dataSource.setUsername(env.getProperty("spring.datasource.postgres.username"));
//        dataSource.setPassword(env.getProperty("spring.datasource.postgres.password"));
//
//        return dataSource;
//    }
//
//    @Bean(name = "postgresEntityManager")
//    public LocalContainerEntityManagerFactoryBean entityManager(
//            @Autowired @Qualifier("postgresDataSource") DataSource dataSource) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        // No postgres entities currently - commented out
//        // em.setPackagesToScan("freeswitch.entity.postgresentity");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//        em.setJpaPropertyMap(properties);
//
//        return em;
//    }
//
//    @Bean(name = "postgresTransactionManager")
//    public JpaTransactionManager transactionManager(
//            @Autowired @Qualifier("postgresEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
//        return new JpaTransactionManager();
//    }
//}