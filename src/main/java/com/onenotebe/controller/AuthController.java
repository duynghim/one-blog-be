package com.onenotebe.controller;

import com.onenotebe.api.ApiResult;
import com.onenotebe.dto.auth.LoginRequest;
import com.onenotebe.dto.auth.LoginResponse;
import com.onenotebe.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
}