package com.onenotebe.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Component that provides a simple per-key rate limiter for registration.
 * Configurable via properties: app.rate-limit.register.requests and window-seconds.
 */
@Component
public class RegistrationRateLimiter {
    private final RateLimiter limiter;

    public RegistrationRateLimiter(
            @Value("${app.rate-limit.register.requests:5}") int maxRequests,
            @Value("${app.rate-limit.register.window-seconds:60}") long windowSeconds
    ) {
        this.limiter = new RateLimiter(maxRequests, Duration.ofSeconds(windowSeconds));
    }

    public boolean allow(String key) {
        return limiter.allow(key);
    }
}