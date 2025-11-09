package com.onenotebe.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's annotation-driven caching to improve performance for
 * frequently accessed resources like blog posts.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}