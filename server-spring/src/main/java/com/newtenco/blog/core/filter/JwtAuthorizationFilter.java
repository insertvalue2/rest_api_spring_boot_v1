package com.newtenco.blog.core.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.newtenco.blog.core.util.JwtUtil;
import com.newtenco.blog.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(JwtUtil.HEADER);

        if (header != null && header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            try {
                User user = JwtUtil.verify(header.substring(JwtUtil.TOKEN_PREFIX.length()));
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
