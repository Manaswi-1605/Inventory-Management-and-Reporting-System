package com.inventory.security;

import com.inventory.dto.AuthRequest;
import com.inventory.dto.AuthResponse;
import com.inventory.dto.RegisterRequest;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import com.inventory.authservice.AuthService;
import com.inventory.utils.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthUtil authUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("dushyant")
                .password("$2a$10$hashedpassword")
                .email("dushyant@test.com")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_newUser_savesAndReturnsToken() {
        RegisterRequest req = new RegisterRequest("dushyant", "pass123", "dushyant@test.com", Role.ADMIN);

        when(userRepository.existsByUsername("dushyant")).thenReturn(false);
        when(userRepository.existsByEmail("dushyant@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(authUtil.generateToken(any(User.class))).thenReturn("jwt.token.here");

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.getToken());
        assertEquals("dushyant", response.getUsername());
        assertEquals("ADMIN", response.getRole());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("pass123");
    }

    @Test
    void register_duplicateUsername_throwsRuntimeException() {
        RegisterRequest req = new RegisterRequest("dushyant", "pass", "new@test.com", Role.CUSTOMER);
        when(userRepository.existsByUsername("dushyant")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(req));
        assertTrue(ex.getMessage().contains("Username already taken"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throwsRuntimeException() {
        RegisterRequest req = new RegisterRequest("newuser", "pass", "dushyant@test.com", Role.CUSTOMER);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("dushyant@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(req));
        assertTrue(ex.getMessage().contains("Email already registered"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_nullRole_defaultsToCustomer() {
        RegisterRequest req = new RegisterRequest("newuser", "pass", "new@test.com", null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertEquals(Role.CUSTOMER, u.getRole()); // verify default applied
            return u;
        });
        when(authUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(req);
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void register_passwordIsEncodedBeforeSave() {
        RegisterRequest req = new RegisterRequest("user", "plaintext", "u@test.com", Role.CUSTOMER);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plaintext")).thenReturn("bcrypt_hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            // Password must NOT be plaintext when saved
            assertNotEquals("plaintext", u.getPassword());
            assertEquals("bcrypt_hashed", u.getPassword());
            return u;
        });
        when(authUtil.generateToken(any())).thenReturn("token");

        authService.register(req);
        verify(passwordEncoder, times(1)).encode("plaintext");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsToken() {
        AuthRequest req = new AuthRequest("dushyant", "pass123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // authenticate() returns Authentication, null means ok here
        when(userRepository.findByUsername("dushyant")).thenReturn(Optional.of(sampleUser));
        when(authUtil.generateToken(sampleUser)).thenReturn("valid.jwt.token");

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("valid.jwt.token", response.getToken());
        assertEquals("dushyant", response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void login_wrongPassword_throwsBadCredentials() {
        AuthRequest req = new AuthRequest("dushyant", "wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(req));

        // Should never reach DB query
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void login_userNotInDB_throwsRuntimeException() {
        AuthRequest req = new AuthRequest("ghost", "pass");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }
}
