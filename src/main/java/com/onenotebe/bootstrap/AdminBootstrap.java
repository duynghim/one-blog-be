package com.onenotebe.bootstrap;

import com.onenotebe.model.Role;
import com.onenotebe.model.User;
import com.onenotebe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public AdminBootstrap(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Read admin credentials from environment/properties with fallback defaults
        String adminUsername = env.getProperty("app.admin.username", "admin");
        String adminPassword = env.getProperty("app.admin.password", "admin123");
        String adminEmail = env.getProperty("app.admin.email", "admin@example.com");

        // Check if admin already exists (by username or email)
        if (userRepository.findByUsername(adminUsername).isEmpty()
                && userRepository.findByEmail(adminEmail).isEmpty()) {

            var admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .role(Role.ROLE_ADMIN)
                    .build();

            // Set system user ID (0 or 1) for bootstrap operations
            // This indicates the entity was created by the system
            admin.setCreatedBy(0L);
            admin.setUpdatedBy(0L);

            userRepository.save(admin);
            log.info("✅ Admin user created with username: {} and email: {}", adminUsername, adminEmail);
        } else {
            log.info("ℹ️ Admin user already exists, skipping creation");
        }
    }
}