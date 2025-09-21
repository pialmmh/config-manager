package com.telcobright.util.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Base repository interface that extends JpaRepository and adds MySQL optimized batch insert.
 *
 * All standard JPA repository methods are inherited:
 * - findById, findAll, findAllById
 * - save, saveAll, saveAndFlush
 * - delete, deleteAll, deleteById
 * - count, existsById
 * - flush, etc.
 *
 * Plus custom MySQL optimized method:
 * - insertExtendedToMysql for bulk inserts
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
@NoRepositoryBean
public interface MySqlOptimizedRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * Performs optimized batch insert using MySQL extended INSERT syntax.
     * This is 5-10x faster than standard JPA saveAll for large datasets.
     *
     * Uses: INSERT INTO table VALUES (...), (...), (...) syntax
     *
     * @param entities List of entities to insert
     * @return Number of entities inserted
     */
    int insertExtendedToMysql(List<T> entities);

    /**
     * Performs optimized batch insert with custom batch size.
     *
     * @param entities List of entities to insert
     * @param batchSize Number of entities to insert per batch
     * @return Number of entities inserted
     */
    int insertExtendedToMysql(List<T> entities, int batchSize);
}