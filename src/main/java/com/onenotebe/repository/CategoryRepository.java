package com.onenotebe.repository;

import com.onenotebe.model.Category;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for Category data access with optimized queries.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a Category by its slug.
     * Using JPQL allows future tuning and is explicit for clarity.
     */
    @Query("select c from Category c where c.slug = :slug")
    Optional<Category> findBySlug(String slug);

    /**
     * Fetch a set of categories by their IDs. Returns a Set to avoid duplicates.
     */
    Set<Category> findByIdIn(Set<Long> ids);
}