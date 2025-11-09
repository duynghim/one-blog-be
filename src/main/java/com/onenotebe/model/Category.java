package com.onenotebe.model;

import com.onenotebe.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Category entity for grouping posts. Extends BaseEntity for ID and auditing.
 * Uses unique constraints on name and slug for consistency and fast lookups.
 */
@Entity
@Table(name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_name", columnNames = {"name"}),
                @UniqueConstraint(name = "uk_category_slug", columnNames = {"slug"})
        },
        indexes = {
                @Index(name = "idx_category_slug", columnList = "slug")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;
}