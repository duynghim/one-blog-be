package com.onenotebe.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * BaseEntity is a mapped superclass that centralizes common persistence fields
 * for all JPA entities in the application. It provides a generated primary key
 * and auditing timestamps for creation and last modification.
 *
 * <p>Design choices:
 * <ul>
 *   <li>@MappedSuperclass ensures no separate table is created for this class,
 *       but its fields are mapped into child entities.</li>
 *   <li>@EntityListeners(AuditingEntityListener.class) enables Spring Data JPA
 *       auditing to automatically populate {@code createdAt} and {@code updatedAt}
 *       without manual intervention.</li>
 *   <li>Fields are {@code protected} to allow direct access in subclasses when
 *       appropriate, while keeping encapsulation flexible via Lombok accessors.</li>
 *   <li>Timestamps use {@link Instant} for a timezone-agnostic, precise time type.</li>
 * </ul>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Primary key using identity strategy for PostgresSQL. The database
     * generates the identifier on insert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Creation timestamp. Populated automatically when the entity is first persisted.
     * Not updatable to preserve audit integrity.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected Instant createdAt;

    /**
     * Last modification timestamp. Updated automatically on each update.
     */
    @LastModifiedDate
    @Column(nullable = false)
    protected Instant updatedAt;

    /**
     * Username of the user who created this entity.
     * Populated automatically by Spring Data JPA auditing.
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected Long createdBy;

    /**
     * Username of the user who last modified this entity.
     * Populated automatically by Spring Data JPA auditing.
     */
    @LastModifiedBy
    @Column(name = "updated_by")
    protected Long updatedBy;
}