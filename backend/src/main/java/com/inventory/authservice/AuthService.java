package com.inventory.authservice;

import com.inventory.dto.AuthRequest;
import com.inventory.dto.AuthResponse;
import com.inventory.dto.RegisterRequest;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import com.inventory.utils.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles register and login business logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthUtil authUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authUtil = authUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user. Defaults to CUSTOMER role if none provided.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Role role = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);
        String token = authUtil.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    /**
     * Authenticate user credentials and return a JWT.
     */
    public AuthResponse login(AuthRequest request) {
        // Throws AuthenticationException if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = authUtil.generateToken(user);
        System.out.println("Login attempt: " + request.getUsername());
        System.out.println("Password entered: " + request.getPassword());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
