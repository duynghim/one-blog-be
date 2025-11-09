package com.onenotebe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration enabling Spring Data auditing for entities extending {@code BaseEntity}.
 *
 * <p>Why this approach:
 * <ul>
 *   <li>@EnableJpaAuditing activates automatic population of {@code createdAt}
 *       and {@code updatedAt} fields via {@code AuditingEntityListener}.</li>
 *   <li>Placed under the root package to be discovered by component scanning.</li>
 * </ul>
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // No explicit beans required for time-based auditing.
}