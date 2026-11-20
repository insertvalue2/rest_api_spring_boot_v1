package com.newtenco.blog.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.newtenco.blog.user.User;

import java.util.Date;

public class JwtUtil {

    public static final String HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24;

    private static final String SECRET = "newtenco-blog-jwt-secret-key-must-be-at-least-64-bytes-long-0123456789";
    private static final Algorithm ALGORITHM = Algorithm.HMAC512(SECRET);

    public static String create(User user) {
        String jwt = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .sign(ALGORITHM);
        return TOKEN_PREFIX + jwt;
    }

    public static User verify(String jwt) {
        DecodedJWT decoded = JWT.require(ALGORITHM).build().verify(jwt);
        Integer id = decoded.getClaim("id").asInt();
        String username = decoded.getSubject();
        return User.builder().id(id).username(username).build();
    }
}
