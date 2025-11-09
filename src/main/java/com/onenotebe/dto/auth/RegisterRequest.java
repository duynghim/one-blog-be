package com.onenotebe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "RegisterRequest", description = "Payload for user registration")
public record RegisterRequest(
        @Schema(description = "Unique username (3-32 chars, [A-Za-z0-9._-])", example = "alice")
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 32, message = "username must be 3-32 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "username may only contain letters, numbers, dot, underscore, hyphen")
        String username,

        @Schema(description = "Email address", example = "alice@example.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,

        @Schema(description = "Password, min 8 chars, letters & digits", example = "Str0ngPass!")
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "password must contain letters and digits"
        )
        String password,

        @Schema(description = "Display name", example = "Alice")
        @Size(max = 64, message = "displayName must be at most 64 characters")
        String displayName
) {}