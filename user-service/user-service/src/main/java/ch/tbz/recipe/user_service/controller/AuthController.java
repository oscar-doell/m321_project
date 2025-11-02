package ch.tbz.recipe.user_service.controller;

import ch.tbz.recipe.user_service.dto.*;
import ch.tbz.recipe.user_service.model.User;
import ch.tbz.recipe.user_service.security.JwtService;
import ch.tbz.recipe.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Validated
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        User u = userService.registerNewUser(req);
        var resp = new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        Optional<User> opt = userService.findByEmail(req.getEmail());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = opt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpServletRequest req) {
        // The JwtAuthenticationFilter sets principal to userId
        Object principal = req.getUserPrincipal();
        if (principal == null) {
            // fallback: use Spring Security context
            var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }
            String userId = (String) authentication.getPrincipal();
            return userService.findById(userId)
                    .map(u -> ResponseEntity.ok(new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt())))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            String userId = req.getUserPrincipal().getName();
            return userService.findById(userId)
                    .map(u -> ResponseEntity.ok(new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt())))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }
}
