package com.example.hospitalmanage.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.hospitalmanage.domain.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.hospitalmanage.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secter}")
    private String secret;


    //jwt generate ->>>>
    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(GET_DRN_SERHIO).withAudience(GET_ARRAYS_ADMINISTRATION)
                .withIssuedAt(new Date()).withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        List<String> autorities = new ArrayList<>();
        for (GrantedAuthority authority:  userPrincipal.getAuthorities()) {
            autorities.add(authority.getAuthority());
        }
        return autorities.toArray(new String[0]);
    }
    ///->>>>>

    public List<GrantedAuthority> getAuthorities(String token) {
       String[]claims = getClaimsForToken(token);
       return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority>authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, null ,authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(jwtVerifier, token);
    }

    private boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {
       Date expiration = jwtVerifier.verify(token).getExpiresAt();
       return expiration.before(new Date());
    }

    public String getSubject(String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        return jwtVerifier.verify(token).getSubject();
    }

    private String[] getClaimsForToken(String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        return jwtVerifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJwtVerifier() {
        JWTVerifier jwtVerifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            jwtVerifier = JWT.require(algorithm).withIssuer(GET_DRN_SERHIO).build();
        } catch (JWTVerificationException e) {
           throw new JWTVerificationException(TOKEN_BE_NOT_VERIFIED);
        }
        return jwtVerifier;
    }


}
