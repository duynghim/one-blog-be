package com.onenotebe.model;

import com.onenotebe.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * User entity storing credentials and role.
 * Note: Password is stored hashed; never log or expose it.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class User extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @ToString.Exclude
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}