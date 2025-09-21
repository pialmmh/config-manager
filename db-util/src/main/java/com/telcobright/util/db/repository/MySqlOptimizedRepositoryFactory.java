package com.telcobright.util.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Factory bean to create MySqlOptimizedRepository instances.
 *
 * Enable in Spring Boot with:
 * @EnableJpaRepositories(repositoryFactoryBeanClass = MySqlOptimizedRepositoryFactory.class)
 */
public class MySqlOptimizedRepositoryFactory<R extends JpaRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public MySqlOptimizedRepositoryFactory(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new MySqlOptimizedJpaRepositoryFactory(entityManager);
    }

    private static class MySqlOptimizedJpaRepositoryFactory extends JpaRepositoryFactory {

        public MySqlOptimizedJpaRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                        EntityManager entityManager) {
            JpaEntityInformation<?, Serializable> entityInformation =
                    getEntityInformation(information.getDomainType());

            return new MySqlOptimizedRepositoryImpl(entityInformation, entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return MySqlOptimizedRepositoryImpl.class;
        }
    }
}