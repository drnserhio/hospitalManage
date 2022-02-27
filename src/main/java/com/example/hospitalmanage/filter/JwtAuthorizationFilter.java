package com.example.hospitalmanage.filter;

import com.example.hospitalmanage.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.hospitalmanage.constant.SecurityConstant.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

@Component
@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
       if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
           response.setStatus(OK.value());
       } else {
           String authenticationHeader = request.getHeader(AUTHORIZATION);

           if (authenticationHeader == null ||
                    !authenticationHeader.startsWith(TOKEN_PREFIX)) {
               filterChain.doFilter(request,response);
               return;
           }

           String token = authenticationHeader.substring(TOKEN_PREFIX.length());
           String username = jwtTokenProvider.getSubject(token);
           if (jwtTokenProvider.isTokenValid(username, token) &&
                   SecurityContextHolder.getContext().getAuthentication() == null) {
               List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
               Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
               SecurityContextHolder.getContext().setAuthentication(authentication);

           } else {
               SecurityContextHolder.clearContext();
           }
       }
        filterChain.doFilter(request,response);
    }
}
