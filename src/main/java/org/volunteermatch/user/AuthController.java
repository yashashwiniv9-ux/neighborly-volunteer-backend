package org.volunteermatch.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.volunteermatch.common.ApiException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(UserRepository users,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/csrf")
    Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserDtos.UserResponse register(@Valid @RequestBody UserDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
        Role role = request.role() == null ? Role.VOLUNTEER : request.role();
        if (role == Role.ORGANIZER && (request.organizationName() == null || request.organizationName().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Organization name is required for organizer accounts.");
        }
        AppUser user = new AppUser(request.name().trim(), email, passwordEncoder.encode(request.password()),
                role, clean(request.city()), role == Role.ORGANIZER ? clean(request.organizationName()) : null);
        return UserDtos.UserResponse.from(users.save(user));
    }

    @PostMapping("/login")
    UserDtos.UserResponse login(@Valid @RequestBody UserDtos.LoginRequest request,
                                HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return UserDtos.UserResponse.from(((UserPrincipal) authentication.getPrincipal()).user());
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
