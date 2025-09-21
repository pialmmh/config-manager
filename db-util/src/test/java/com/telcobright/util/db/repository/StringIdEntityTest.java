package com.telcobright.util.db.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test String ID support as required by FINAL_REQUIREMENTS.md
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/testdb?useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.username=root",
    "spring.datasource.password=123456",
    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StringIdEntityTest {

    @Autowired
    private UserRepository userRepository;

    @SpringBootApplication
    @EnableJpaRepositories(
        basePackages = "com.telcobright.util.db.repository",
        repositoryFactoryBeanClass = MySqlOptimizedRepositoryFactory.class
    )
    @EntityScan("com.telcobright.util.db.repository")
    static class TestConfig {
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Test String ID entity with standard JPA operations")
    void testStringIdWithStandardJpa() {
        // Create user with String ID
        User user = new User("USR001", "John Doe", "john@example.com");

        // Test save
        User saved = userRepository.save(user);
        assertEquals("USR001", saved.getId());

        // Test findById
        Optional<User> found = userRepository.findById("USR001");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());

        // Test exists
        assertTrue(userRepository.existsById("USR001"));
    }

    @Test
    @Order(2)
    @DisplayName("Test insertExtendedToMysql with String IDs")
    @Transactional
    void testInsertExtendedWithStringIds() {
        List<User> users = generateUsers(1000);

        long startTime = System.currentTimeMillis();
        int inserted = userRepository.insertExtendedToMysql(users);
        long duration = System.currentTimeMillis() - startTime;

        assertEquals(1000, inserted);
        assertEquals(1000, userRepository.count());

        System.out.println("String ID batch insert (1000 records): " + duration + "ms");

        // Verify data
        User found = userRepository.findById("USER-500").orElse(null);
        assertNotNull(found);
        assertEquals("User 500", found.getName());
    }

    @Test
    @Order(3)
    @DisplayName("Test large batch with String IDs (100,000 entities)")
    @Transactional
    void testLargeBatchWithStringIds() {
        List<User> users = generateUsers(100000);

        long startTime = System.currentTimeMillis();
        int inserted = userRepository.insertExtendedToMysql(users);
        long duration = System.currentTimeMillis() - startTime;

        assertEquals(100000, inserted);
        assertEquals(100000, userRepository.count());

        System.out.println("Large batch with String IDs (100,000 records): " + duration + "ms");
        assertTrue(duration < 30000, "Should complete within 30 seconds");
    }

    @Test
    @Order(4)
    @DisplayName("Test performance comparison: saveAll vs insertExtendedToMysql")
    @Transactional
    void testPerformanceComparison() {
        List<User> batch1 = generateUsers(10000);
        List<User> batch2 = generateUsers(10000);

        // Standard JPA saveAll
        long saveAllStart = System.currentTimeMillis();
        userRepository.saveAll(batch1);
        long saveAllDuration = System.currentTimeMillis() - saveAllStart;

        userRepository.deleteAll();

        // MySQL extended insert
        long mysqlStart = System.currentTimeMillis();
        userRepository.insertExtendedToMysql(batch2);
        long mysqlDuration = System.currentTimeMillis() - mysqlStart;

        double speedup = (double) saveAllDuration / mysqlDuration;

        System.out.println("\n=== Performance Results (10,000 String ID entities) ===");
        System.out.println("Standard JPA saveAll: " + saveAllDuration + "ms");
        System.out.println("MySQL extended insert: " + mysqlDuration + "ms");
        System.out.printf("Speedup: %.2fx\n", speedup);
        System.out.println("====================================================\n");

        assertTrue(speedup >= 5.0, "Should be at least 5x faster");
    }

    @Test
    @Order(5)
    @DisplayName("Test custom queries with String ID entities")
    void testCustomQueries() {
        // Insert test data
        List<User> users = Arrays.asList(
            new User("ADM001", "Admin User", "admin@example.com"),
            new User("USR001", "Regular User", "user@example.com"),
            new User("USR002", "Another User", "another@example.com")
        );
        userRepository.insertExtendedToMysql(users);

        // Test derived query
        List<User> foundByName = userRepository.findByName("Admin User");
        assertEquals(1, foundByName.size());
        assertEquals("ADM001", foundByName.get(0).getId());

        // Test JPQL query
        List<User> searchResults = userRepository.searchByEmail("user");
        assertEquals(2, searchResults.size());

        // Test native query
        List<User> recentUsers = userRepository.findRecentUsers();
        assertEquals(3, recentUsers.size());
    }

    private List<User> generateUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new User(
                "USER-" + i,
                "User " + i,
                "user" + i + "@example.com"
            ))
            .collect(Collectors.toList());
    }

    // User entity with String ID
    @Entity
    @Table(name = "users")
    public static class User {
        @Id
        private String id;  // String ID, not auto-generated

        @Column(name = "name")
        private String name;

        @Column(name = "email")
        private String email;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        public User() {
            this.createdAt = LocalDateTime.now();
        }

        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.createdAt = LocalDateTime.now();
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    // Repository with String ID
    public interface UserRepository extends MySqlOptimizedRepository<User, String> {
        List<User> findByName(String name);

        @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword%")
        List<User> searchByEmail(@Param("keyword") String keyword);

        @Query(value = "SELECT * FROM users ORDER BY created_at DESC", nativeQuery = true)
        List<User> findRecentUsers();
    }
}