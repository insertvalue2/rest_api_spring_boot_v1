# jwt-auth 코드 요약

## T1 토큰을 검사하는 필터와 내 정보 API
커밋: 7e13dcb

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index 29a22fe..9e62001 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -1,12 +1,15 @@
 package com.newtenco.blog.core;
 
+import com.newtenco.blog.core.filter.JwtAuthorizationFilter;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
+import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
+import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 import org.springframework.web.cors.CorsConfiguration;
 import org.springframework.web.cors.CorsConfigurationSource;
 import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
@@ -47,6 +50,10 @@ public class SecurityConfig {
         http.formLogin(form -> form.disable());
         http.httpBasic(basic -> basic.disable());
 
+        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
+
+        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
+
         http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
 
         return http.build();
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java
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
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(JwtUtil.HEADER);

        if (header != null && header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            try {
                User user = JwtUtil.verify(header.substring(JwtUtil.TOKEN_PREFIX.length()));
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java b/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
index f7a3dd6..48d37b8 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.core.util;
 
 import com.auth0.jwt.JWT;
 import com.auth0.jwt.algorithms.Algorithm;
+import com.auth0.jwt.interfaces.DecodedJWT;
 import com.newtenco.blog.user.User;
 
 import java.util.Date;
@@ -24,4 +25,11 @@ public class JwtUtil {
                 .sign(ALGORITHM);
         return TOKEN_PREFIX + jwt;
     }
+
+    public static User verify(String jwt) {
+        DecodedJWT decoded = JWT.require(ALGORITHM).build().verify(jwt);
+        Integer id = decoded.getClaim("id").asInt();
+        String username = decoded.getSubject();
+        return User.builder().id(id).username(username).build();
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index 9da0aa8..cc421fa 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -5,6 +5,7 @@ import com.newtenco.blog.core.util.Resp;
 import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
+import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
@@ -30,6 +31,11 @@ public class UserController {
                 .body(new Resp<>(200, "성공", loginDTO.user()));
     }
 
+    @GetMapping("/api/users/me")
+    public ResponseEntity<?> me(@AuthenticationPrincipal User sessionUser) {
+        return Resp.ok(userService.내정보(sessionUser.getId()));
+    }
+
     @GetMapping("/check-username")
     public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
         return Resp.ok(userService.유저네임중복체크(username));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index 5e67aca..a5bad84 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -1,6 +1,7 @@
 package com.newtenco.blog.user;
 
 import com.newtenco.blog.core.handler.errors.BadRequestException;
+import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.core.handler.errors.UnauthorizedException;
 import com.newtenco.blog.core.util.JwtUtil;
 import lombok.RequiredArgsConstructor;
@@ -36,6 +37,12 @@ public class UserService {
         return new UserResponse.LoginDTO(JwtUtil.create(user), new UserResponse.DTO(user));
     }
 
+    public UserResponse.DTO 내정보(Integer id) {
+        User user = userRepository.findById(id)
+                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
+        return new UserResponse.DTO(user);
+    }
+
     public UserResponse.AvailableDTO 유저네임중복체크(String username) {
         boolean available = !userRepository.existsByUsername(username);
         return new UserResponse.AvailableDTO(available);
```

## T2 로그인이 필요한 주소 막기
커밋: 7b6f27d

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/util/RespFilter.java | 새로 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index 9e62001..b26085f 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -1,9 +1,11 @@
 package com.newtenco.blog.core;
 
 import com.newtenco.blog.core.filter.JwtAuthorizationFilter;
+import com.newtenco.blog.core.util.RespFilter;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
+import org.springframework.http.HttpMethod;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@@ -54,7 +56,17 @@ public class SecurityConfig {
 
         http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
 
-        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
+        http.exceptionHandling(ex -> ex
+                .authenticationEntryPoint((req, res, e) -> RespFilter.fail(res, 401, "로그인 후 이용해주세요")));
+
+        http.authorizeHttpRequests(auth -> auth
+                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
+                .requestMatchers("/join", "/login", "/check-username").permitAll()
+                .requestMatchers("/h2-console/**").permitAll()
+                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
+                .requestMatchers("/api/**").authenticated()
+                .anyRequest().permitAll()
+        );
 
         return http.build();
     }
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/util/RespFilter.java
package com.newtenco.blog.core.util;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

public class RespFilter {

    private static final JsonMapper mapper = JsonMapper.builder().build();

    public static void fail(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(mapper.writeValueAsString(new Resp<>(status, msg, null)));
    }
}
```

## T3 규칙에 없는 주소 막기
커밋: 31d9d35

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index b26085f..dd32ec5 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -57,7 +57,8 @@ public class SecurityConfig {
         http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
 
         http.exceptionHandling(ex -> ex
-                .authenticationEntryPoint((req, res, e) -> RespFilter.fail(res, 401, "로그인 후 이용해주세요")));
+                .authenticationEntryPoint((req, res, e) -> RespFilter.fail(res, 401, "로그인 후 이용해주세요"))
+                .accessDeniedHandler((req, res, e) -> RespFilter.fail(res, 403, "권한이 없습니다")));
 
         http.authorizeHttpRequests(auth -> auth
                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
@@ -65,7 +66,7 @@ public class SecurityConfig {
                 .requestMatchers("/h2-console/**").permitAll()
                 .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                 .requestMatchers("/api/**").authenticated()
-                .anyRequest().permitAll()
+                .anyRequest().denyAll()
         );
 
         return http.build();
```

## 테스트 보강
커밋: 0f18338
- 학생 코드 변경 없음
