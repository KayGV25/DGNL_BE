package com.dgnl_backend.project.dgnl_backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dgnl_backend.project.dgnl_backend.repositories.identity.TokenRepository;
import com.dgnl_backend.project.dgnl_backend.services.UserDetailService;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter{

    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        if (token!=null && !token.isBlank() && tokenRepository.existsByToken(token)) {
            try{
                Claims claims = jwtUtils.decode(token);
                String id = claims.getSubject();
                
                UserDetails userDetails = userDetailService.loadUserByUsername(id);

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                        
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            } catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }

        }

        filterChain.doFilter(request, response);
    }
}
