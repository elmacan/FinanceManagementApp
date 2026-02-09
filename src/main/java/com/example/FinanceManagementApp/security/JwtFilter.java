package com.example.FinanceManagementApp.security;

import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.UsersRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsersRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token=null;
        String email=null;
        Long userId=null;

    try {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);
            userId = jwtService.extractUserId(token);
        }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtService.isTokenValid(token)) {

                Users user = userRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                CurrentUserPrincipal principal = new CurrentUserPrincipal(user);


                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal, null, List.of());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }catch (Exception e){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
            {
              "status": 401,
              "error": "Unauthorized",
              "message": "Token expired or invalid",
              "path": "%s"
            }
            """.formatted(request.getRequestURI()));

    }
    }


}