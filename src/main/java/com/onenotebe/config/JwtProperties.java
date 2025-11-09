package com.onenotebe.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly typed JWT configuration properties.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /** Secret key for HS256 signing (never commit real secrets). */
    private String secret;
    /** Expiration time in milliseconds. */
    private long expirationMs;

}