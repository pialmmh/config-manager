package com.telcobright.util.db.example;

import com.telcobright.util.db.repository.MySqlOptimizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Example repository extending MySqlOptimizedRepository.
 *
 * Automatically inherits:
 * - All JpaRepository methods (findById, save, delete, etc.)
 * - insertExtendedToMysql for optimized batch inserts
 *
 * Plus custom JPQL queries defined below.
 */
@Repository
public interface ProductRepository extends MySqlOptimizedRepository<Product, Long> {

    // Standard derived query methods
    List<Product> findByCategory(String category);

    List<Product> findByActiveTrue();

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    Product findByProductCode(String productCode);

    // Custom JPQL queries
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.active = true")
    List<Product> findActiveByCategoryJPQL(@Param("category") String category);

    @Query("SELECT p FROM Product p WHERE p.price > :minPrice ORDER BY p.price ASC")
    List<Product> findProductsAbovePrice(@Param("minPrice") BigDecimal minPrice);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countProductsByCategory();

    // Native SQL query example
    @Query(value = "SELECT * FROM products WHERE quantity < :threshold", nativeQuery = true)
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    // Custom update query
    @Query("UPDATE Product p SET p.quantity = p.quantity - :amount WHERE p.id = :productId")
    void decrementStock(@Param("productId") Long productId, @Param("amount") int amount);
}