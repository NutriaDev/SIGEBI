package sigebi.users.services.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import sigebi.users.security.JwtAuthorizationFilter;
import sigebi.users.security.JwtUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class JwtAuthorizationFilterTest {

    JwtUtils jwtUtils = mock(JwtUtils.class);
    JwtAuthorizationFilter filter;

    @BeforeEach
    void clearBefore() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setup() {
        filter = new JwtAuthorizationFilter(jwtUtils);
    }


    @Test
    void doFilter_noHeader_continuesChain() throws ServletException, IOException {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_invalidToken_continuesChain() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtils.isValid("token")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_validToken_setsAuthentication() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtils.isValid("token")).thenReturn(true);
        when(jwtUtils.getUserId("token")).thenReturn(1L);
        when(jwtUtils.getRoles("token")).thenReturn(List.of("ADMIN"));
        when(jwtUtils.getPermissions("token"))
                .thenReturn(List.of("users.create.admin"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_headerWithoutBearer_continuesChain() throws Exception {

        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic something");

        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @AfterEach
    void clearAfter() {
        SecurityContextHolder.clearContext();
    }
}

