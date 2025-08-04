package com.base.infra.config.security.filter;

import com.base.domain.user.domain.Account;
import com.base.domain.user.repository.AccountRepository;
import com.base.infra.config.security.service.JwtService;
import com.base.infra.user.entity.AccountEntity;
import com.base.infra.user.repository.JpaAccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final JpaAccountRepository accountJpaRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Log để debug
        System.out.println("Request path: " + path + ", method: " + method);

        // Public endpoints - Don't need filter
        boolean shouldSkip = path.equals("/") ||
                path.equals("/error") ||
                path.equals("/favicon.ico") ||

                // Auth endpoints
                path.startsWith("/api/auth/") ||

                // Test endpoints
                path.startsWith("/api/test/") ||

                // H2 Console
                path.startsWith("/h2-console/") ||

                // Swagger UI - Tất cả các path có thể
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/configuration/") ||
                path.equals("/swagger-config") ||
                path.startsWith("/api-docs");

        if (shouldSkip) {
            System.out.println("Skipping filter for path: " + path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String jwt;
        String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AccountEntity accountEntity = accountJpaRepository.findByUsername(username).orElse(null);

            if (accountEntity != null) {
                Account domainAccount = accountRepository.findByUsername(username).orElse(null);

                if (domainAccount != null && jwtService.validateToken(jwt, domainAccount)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(accountEntity, null, accountEntity.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}