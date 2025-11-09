package com.onenotebe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA configuration for auditing support.
 * Enables automatic population of @CreatedDate, @LastModifiedDate,
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    /**
     * Provides the current auditor (user ID) for JPA auditing.
     * Returns the authenticated user's ID, or null for system operations.
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                // Return null for system operations (like bootstrap)
                // This allows nullable columns to work
                return Optional.empty();
            }

            // Extract user ID from authentication principal
            // Adjust this based on your UserDetails implementation
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                // For bootstrap or when user ID is not available, return null
                return Optional.empty();
            }

            // If you have a custom UserDetails with ID, extract it here
            return Optional.empty();
        };
    }
}