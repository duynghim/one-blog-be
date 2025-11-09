package com.onenotebe.controller;

import com.onenotebe.api.ApiResult;
import com.onenotebe.dto.auth.LoginRequest;
import com.onenotebe.dto.auth.LoginResponse;
import com.onenotebe.dto.auth.RegisterRequest;
import com.onenotebe.dto.auth.RegisterResponse;
import com.onenotebe.exception.RateLimitExceededException;
import com.onenotebe.security.JwtService;
import com.onenotebe.security.RegistrationRateLimiter;
import com.onenotebe.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;
    private final RegistrationRateLimiter registrationRateLimiter;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          AuthService authService,
                          RegistrationRateLimiter registrationRateLimiter) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
        this.registrationRateLimiter = registrationRateLimiter;
    }

    @Operation(
            summary = "Login and receive JWT",
            description = "Validates credentials and returns a JWT token.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt [username={}]", request.username());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails ud) {
                username = ud.getUsername();
            } else {
                username = principal.toString();
            }
            var role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");
            var token = jwtService.generateToken(username, role);
            log.info("User logged in [username={}]", username);
            return ResponseEntity.ok(ApiResult.success(new LoginResponse(token)));
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user [{}]", request.username());
            // GlobalExceptionHandler will format AuthenticationException; we return 401 via exception
            throw e;
        }
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with ROLE_USER."
    )
    @ApiResponse(responseCode = "201", description = "Registration successful",
            content = @Content(schema = @Schema(implementation = RegisterResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "Duplicate username or email")
    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public ResponseEntity<ApiResult<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                                HttpServletRequest httpRequest) {
        var ip = clientIp(httpRequest);
        if (!registrationRateLimiter.allow(ip)) {
            throw new RateLimitExceededException("Too many registration attempts. Please try again later.");
        }
        var response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success(response));
    }

    private String clientIp(HttpServletRequest request) {
        var forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}