package com.telcobright.util.db;

import com.telcobright.util.db.example.Product;
import com.telcobright.util.db.example.ProductRepository;
import com.telcobright.util.db.repository.MySqlOptimizedRepositoryFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MySqlOptimizedRepository
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
public class MySqlOptimizedRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @SpringBootApplication
    @EnableJpaRepositories(
        basePackages = "com.telcobright.util.db.example",
        repositoryFactoryBeanClass = MySqlOptimizedRepositoryFactory.class
    )
    @EntityScan("com.telcobright.util.db.example")
    static class TestConfig {
        // Test configuration
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Test standard JPA repository methods")
    void testStandardJpaMethods() {
        // Test save
        Product product = new Product("PROD001", "Test Product", new BigDecimal("99.99"));
        product.setCategory("Electronics");
        product.setQuantity(100);

        Product saved = productRepository.save(product);
        assertNotNull(saved.getId());

        // Test findById
        Optional<Product> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("PROD001", found.get().getProductCode());

        // Test count
        assertEquals(1, productRepository.count());

        // Test existsById
        assertTrue(productRepository.existsById(saved.getId()));

        // Test delete
        productRepository.deleteById(saved.getId());
        assertEquals(0, productRepository.count());
    }

    @Test
    @Order(2)
    @DisplayName("Test insertExtendedToMysql - basic")
    @Transactional
    void testInsertExtendedToMysqlBasic() {
        List<Product> products = generateProducts(100);

        long startTime = System.currentTimeMillis();
        int inserted = productRepository.insertExtendedToMysql(products);
        long duration = System.currentTimeMillis() - startTime;

        assertEquals(100, inserted);
        assertEquals(100, productRepository.count());

        System.out.println("MySQL extended insert (100 records): " + duration + "ms");

        // Verify data
        Product found = productRepository.findByProductCode("BATCH000050");
        assertNotNull(found);
        assertEquals("Batch Product 50", found.getName());
    }

    @Test
    @Order(3)
    @DisplayName("Test insertExtendedToMysql - large batch")
    @Transactional
    void testInsertExtendedToMysqlLargeBatch() {
        List<Product> products = generateProducts(10000);

        long startTime = System.currentTimeMillis();
        int inserted = productRepository.insertExtendedToMysql(products, 5000);
        long duration = System.currentTimeMillis() - startTime;

        assertEquals(10000, inserted);
        assertEquals(10000, productRepository.count());

        System.out.println("MySQL extended insert (10000 records): " + duration + "ms");
    }

    @Test
    @Order(4)
    @DisplayName("Test performance comparison: saveAll vs insertExtendedToMysql")
    @Transactional
    void testPerformanceComparison() {
        List<Product> products1 = generateProducts(1000);
        List<Product> products2 = generateProducts(1000);

        // Test standard saveAll
        long standardStart = System.currentTimeMillis();
        productRepository.saveAll(products1);
        long standardDuration = System.currentTimeMillis() - standardStart;

        productRepository.deleteAll();

        // Test MySQL extended insert
        long mysqlStart = System.currentTimeMillis();
        productRepository.insertExtendedToMysql(products2);
        long mysqlDuration = System.currentTimeMillis() - mysqlStart;

        double speedup = (double) standardDuration / mysqlDuration;

        System.out.println("\n=== Performance Comparison (1000 records) ===");
        System.out.println("Standard saveAll: " + standardDuration + "ms");
        System.out.println("MySQL extended insert: " + mysqlDuration + "ms");
        System.out.printf("Speedup: %.2fx\n", speedup);
        System.out.println("==========================================\n");

        assertTrue(mysqlDuration <= standardDuration,
            "MySQL extended insert should be at least as fast as standard saveAll");
    }

    @Test
    @Order(5)
    @DisplayName("Test custom JPQL queries")
    @Transactional
    void testCustomJpqlQueries() {
        // Insert test data
        List<Product> products = new ArrayList<>();
        products.add(createProduct("ELEC001", "Laptop", new BigDecimal("999.99"), "Electronics", 10));
        products.add(createProduct("ELEC002", "Phone", new BigDecimal("599.99"), "Electronics", 20));
        products.add(createProduct("BOOK001", "Java Book", new BigDecimal("49.99"), "Books", 50));
        products.add(createProduct("BOOK002", "Spring Book", new BigDecimal("39.99"), "Books", 30));
        productRepository.insertExtendedToMysql(products);

        // Test findByCategory
        List<Product> electronics = productRepository.findByCategory("Electronics");
        assertEquals(2, electronics.size());

        // Test findByActiveTrue
        List<Product> activeProducts = productRepository.findByActiveTrue();
        assertEquals(4, activeProducts.size());

        // Test findByPriceBetween
        List<Product> midRange = productRepository.findByPriceBetween(
            new BigDecimal("40"), new BigDecimal("600")
        );
        assertEquals(3, midRange.size());

        // Test custom JPQL
        List<Product> activeByCategoryJPQL = productRepository.findActiveByCategoryJPQL("Electronics");
        assertEquals(2, activeByCategoryJPQL.size());

        // Test findProductsAbovePrice
        List<Product> expensive = productRepository.findProductsAbovePrice(new BigDecimal("500"));
        assertEquals(2, expensive.size());

        // Test countProductsByCategory
        List<Object[]> categoryCounts = productRepository.countProductsByCategory();
        assertEquals(2, categoryCounts.size());
    }

    @Test
    @Order(6)
    @DisplayName("Test native SQL query")
    @Transactional
    void testNativeSqlQuery() {
        // Insert test data
        List<Product> products = new ArrayList<>();
        products.add(createProduct("LOW001", "Low Stock Item", new BigDecimal("10.00"), "General", 5));
        products.add(createProduct("HIGH001", "High Stock Item", new BigDecimal("20.00"), "General", 100));
        productRepository.insertExtendedToMysql(products);

        // Test native SQL query
        List<Product> lowStock = productRepository.findLowStockProducts(10);
        assertEquals(1, lowStock.size());
        assertEquals("LOW001", lowStock.get(0).getProductCode());
    }

    @Test
    @Order(7)
    @DisplayName("Test empty list handling")
    void testEmptyListHandling() {
        List<Product> emptyList = new ArrayList<>();
        int inserted = productRepository.insertExtendedToMysql(emptyList);
        assertEquals(0, inserted);
    }

    @Test
    @Order(8)
    @DisplayName("Test null safety")
    void testNullSafety() {
        int inserted = productRepository.insertExtendedToMysql(null);
        assertEquals(0, inserted);
    }

    // Helper methods

    private List<Product> generateProducts(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> {
                Product p = new Product(
                    "BATCH" + String.format("%06d", i),
                    "Batch Product " + i,
                    new BigDecimal((i % 1000) + ".99")
                );
                p.setCategory(i % 2 == 0 ? "Category A" : "Category B");
                p.setQuantity(i % 100);
                p.setDescription("Description for product " + i);
                return p;
            })
            .toList();
    }

    private Product createProduct(String code, String name, BigDecimal price, String category, int quantity) {
        Product p = new Product(code, name, price);
        p.setCategory(category);
        p.setQuantity(quantity);
        return p;
    }
}