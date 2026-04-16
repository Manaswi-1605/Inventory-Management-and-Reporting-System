package com.inventory.security;

import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import com.inventory.authservice.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("dushyant")
                .password("$2a$10$hashed")
                .email("d@test.com")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        when(userRepository.findByUsername("dushyant")).thenReturn(Optional.of(sampleUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("dushyant");

        assertNotNull(result);
        assertEquals("dushyant", result.getUsername());
        assertTrue(result.isEnabled());
    }

    @Test
    void loadUserByUsername_existingUser_hasCorrectAuthority() {
        when(userRepository.findByUsername("dushyant")).thenReturn(Optional.of(sampleUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("dushyant");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_unknownUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("ghost"));
        assertTrue(ex.getMessage().contains("ghost"));
    }

    @Test
    void loadUserByUsername_inactiveUser_isEnabledFalse() {
        sampleUser.setActive(false);
        when(userRepository.findByUsername("dushyant")).thenReturn(Optional.of(sampleUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("dushyant");

        assertFalse(result.isEnabled());
    }

    @Test
    void loadUserByUsername_customerRole_hasCustomerAuthority() {
        sampleUser.setRole(Role.CUSTOMER);
        when(userRepository.findByUsername("dushyant")).thenReturn(Optional.of(sampleUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("dushyant");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
        assertFalse(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
