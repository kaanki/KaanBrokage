package com.example.KaanBrokage.security;

import com.example.KaanBrokage.service.CustomerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomerService customerService;
    private static final String SECRET_KEY = "721a5e5946792fe87efdc1e3e92a7141196e8520f558dd9d99706c63053ad600";

    public JwtAuthenticationFilter(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String customerId = claims.getSubject();
            String role = claims.get("ROLE", String.class);


            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            var authToken = new UsernamePasswordAuthenticationToken(
                    customerId,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            // Token invalid veya expired
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
