package com.onenotebe.model;

import com.onenotebe.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple test entity extending BaseEntity to verify inheritance and auditing.
 */
@Entity
@Table(name = "test_notes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestNote extends BaseEntity {

    @Column(nullable = false)
    private String title;
}