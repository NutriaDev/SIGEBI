package sigebi.users.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 🔎 PASO 1 — Ver si el header llega
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 🔎 PASO 2 — Validación
        boolean valid = jwtUtils.isValid(token);


        if (!valid) {

            filterChain.doFilter(request, response);
            return;
        }

        // 🔎 PASO 3 — Claims
        var claims = jwtUtils.getClaims(token);


        Long userId = jwtUtils.getUserId(token);
        List<String> roles = jwtUtils.getRoles(token);
        List<String> permissions = jwtUtils.getPermissions(token);



        var authorities = Stream.concat(
                        roles.stream().map(role -> "ROLE_" + role),
                        permissions.stream()
                ).map(SimpleGrantedAuthority::new)
                .toList();


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);



        filterChain.doFilter(request, response);
    }



}
