package com.onenotebe.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory rate limiter for low-volume endpoints like registration.
 * Not a replacement for a production-grade solution, but adequate for demo.
 */
public class RateLimiter {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final Duration windowSize;

    public RateLimiter(int maxRequests, Duration windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    public boolean allow(String key) {
        var now = Instant.now();
        var w = windows.compute(key, (k, existing) -> {
            if (existing == null || now.isAfter(existing.resetAt)) {
                return new Window(1, now.plus(windowSize));
            }
            if (existing.count < maxRequests) {
                existing.count++;
                return existing;
            }
            return existing; // full
        });
        return w.count <= maxRequests;
    }

    private static final class Window {
        int count;
        Instant resetAt;

        Window(int count, Instant resetAt) {
            this.count = count;
            this.resetAt = resetAt;
        }
    }
}