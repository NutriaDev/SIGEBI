package sigebi.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.extension.ExtendWith;
import sigebi.auth.service.JwtService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock JwtService jwtService;
    @Mock FilterChain filterChain;

    JwtAuthorizationFilter filter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthorizationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    // 🔥 shouldNotFilter - OPTIONS
    @Test
    void shouldNotFilter_whenOptions_returnsTrue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("OPTIONS");

        assertTrue(filter.shouldNotFilter(request));
    }

    // 🔥 shouldNotFilter - login endpoint
    @Test
    void shouldNotFilter_whenLoginPath_returnsTrue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/auth/login");

        assertTrue(filter.shouldNotFilter(request));
    }

    // 🔥 header null
    @Test
    void doFilter_whenNoHeader_continuesChain() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 🔥 header sin Bearer
    @Test
    void doFilter_whenHeaderWithoutBearer_continuesChain() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 🔥 token inválido
    @Test
    void doFilter_whenInvalidToken_continuesChain() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isValid("token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 🔥 token válido
    @Test
    void doFilter_whenValidToken_setsAuthentication() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.isValid("token")).thenReturn(true);
        when(jwtService.getUserId("token")).thenReturn(1L);
        when(jwtService.getRoles("token")).thenReturn(List.of("ADMIN"));
        when(jwtService.getPermissions("token"))
                .thenReturn(List.of("users.create.admin"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertEquals(1L, auth.getPrincipal());
        assertEquals(2, auth.getAuthorities().size());
    }
}
