package com.inventory.security;

import com.inventory.security.JwtAuthenticationFilter;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.authservice.CustomUserDetailsService;
import com.inventory.utils.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private AuthUtil authUtil;
    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L).username("dushyant").password("hashed")
                .email("d@test.com").role(Role.ADMIN).active(true).build();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Clean up SecurityContext after each test
        SecurityContextHolder.clearContext();
    }

    // ── No / invalid Authorization header ────────────────────────────────────

    @Test
    void doFilter_noAuthHeader_proceedsWithoutAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_headerNotStartingWithBearer_proceedsWithoutAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_malformedToken_proceedsWithoutAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer not.a.real.token");
        when(authUtil.extractUsername("not.a.real.token"))
                .thenThrow(new RuntimeException("Invalid JWT"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // ── Valid token ────────────────────────────────────────────────────────────

    @Test
    void doFilter_validToken_setsAuthentication() throws Exception {
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authUtil.extractUsername(token)).thenReturn("dushyant");
        when(customUserDetailsService.loadUserByUsername("dushyant")).thenReturn(sampleUser);
        when(authUtil.isTokenValid(token, sampleUser)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("dushyant",
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilter_validToken_authenticationHasAdminAuthority() throws Exception {
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authUtil.extractUsername(token)).thenReturn("dushyant");
        when(customUserDetailsService.loadUserByUsername("dushyant")).thenReturn(sampleUser);
        when(authUtil.isTokenValid(token, sampleUser)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertTrue(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // ── Invalid / expired token ────────────────────────────────────────────────

    @Test
    void doFilter_expiredToken_doesNotSetAuthentication() throws Exception {
        String token = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authUtil.extractUsername(token)).thenReturn("dushyant");
        when(customUserDetailsService.loadUserByUsername("dushyant")).thenReturn(sampleUser);
        when(authUtil.isTokenValid(token, sampleUser)).thenReturn(false); // expired

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_tokenForDifferentUser_doesNotSetAuthentication() throws Exception {
        String token = "wrong.user.token";
        User otherUser = User.builder()
                .id(2L).username("other").password("h").email("o@t.com")
                .role(Role.CUSTOMER).active(true).build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authUtil.extractUsername(token)).thenReturn("other");
        when(customUserDetailsService.loadUserByUsername("other")).thenReturn(otherUser);
        when(authUtil.isTokenValid(token, otherUser)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_alwaysCallsFilterChain() throws Exception {
        // FilterChain must always be called — even on invalid tokens
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(authUtil.extractUsername("bad.token")).thenThrow(new RuntimeException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
