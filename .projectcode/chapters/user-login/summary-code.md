# user-login 코드 요약

## T1 로그인 API 만들기
커밋: f578c7b

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/UnauthorizedException.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index 97bebe1..ca0334e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.core.handler;
 
 import com.newtenco.blog.core.handler.errors.BadRequestException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
+import com.newtenco.blog.core.handler.errors.UnauthorizedException;
 import com.newtenco.blog.core.util.Resp;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.HttpStatus;
@@ -33,6 +34,11 @@ public class GlobalExceptionHandler {
         return Resp.fail(HttpStatus.BAD_REQUEST, e.getMessage());
     }
 
+    @ExceptionHandler(UnauthorizedException.class)
+    public ResponseEntity<?> ex401(UnauthorizedException e) {
+        return Resp.fail(HttpStatus.UNAUTHORIZED, e.getMessage());
+    }
+
     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<?> ex404(NotFoundException e) {
         return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/handler/errors/UnauthorizedException.java
package com.newtenco.blog.core.handler.errors;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index f14a36b..f8f0de5 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -21,6 +21,11 @@ public class UserController {
         return Resp.ok(userService.회원가입(reqDTO));
     }
 
+    @PostMapping("/login")
+    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO reqDTO) {
+        return Resp.ok(userService.로그인(reqDTO));
+    }
+
     @GetMapping("/check-username")
     public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
         return Resp.ok(userService.유저네임중복체크(username));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
index 608e8b8..4a732c4 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
@@ -2,7 +2,11 @@ package com.newtenco.blog.user;
 
 import org.springframework.data.jpa.repository.JpaRepository;
 
+import java.util.Optional;
+
 public interface UserRepository extends JpaRepository<User, Integer> {
 
     boolean existsByUsername(String username);
+
+    Optional<User> findByUsername(String username);
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index 5d5fa01..e318582 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -27,4 +27,7 @@ public class UserRequest {
                     .build();
         }
     }
+
+    public record LoginDTO(String username, String password) {
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index 7c0f81d..fc07c5a 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -1,6 +1,7 @@
 package com.newtenco.blog.user;
 
 import com.newtenco.blog.core.handler.errors.BadRequestException;
+import com.newtenco.blog.core.handler.errors.UnauthorizedException;
 import lombok.RequiredArgsConstructor;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;
@@ -24,6 +25,16 @@ public class UserService {
         return new UserResponse.DTO(savedUser);
     }
 
+    public UserResponse.DTO 로그인(UserRequest.LoginDTO reqDTO) {
+        User user = userRepository.findByUsername(reqDTO.username())
+                .orElseThrow(() -> new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다"));
+
+        if (!passwordEncoder.matches(reqDTO.password(), user.getPassword())) {
+            throw new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다");
+        }
+        return new UserResponse.DTO(user);
+    }
+
     public UserResponse.AvailableDTO 유저네임중복체크(String username) {
         boolean available = !userRepository.existsByUsername(username);
         return new UserResponse.AvailableDTO(available);
```

## T2 JWT 만들어 헤더에 담기
커밋: 6016e4a

| 파일 | 변경 |
|---|---|
| server-spring/build.gradle | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/build.gradle b/server-spring/build.gradle
index b4e5e73..0d890c5 100644
--- a/server-spring/build.gradle
+++ b/server-spring/build.gradle
@@ -18,6 +18,7 @@ repositories {
 }
 
 dependencies {
+    implementation 'com.auth0:java-jwt:4.5.0'
     implementation 'org.springframework.boot:spring-boot-h2console'
     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
     implementation 'org.springframework.boot:spring-boot-starter-security'
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
package com.newtenco.blog.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index f8f0de5..93e41f0 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.user;
 
+import com.newtenco.blog.core.util.JwtUtil;
 import com.newtenco.blog.core.util.Resp;
 import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
@@ -23,7 +24,10 @@ public class UserController {
 
     @PostMapping("/login")
     public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO reqDTO) {
-        return Resp.ok(userService.로그인(reqDTO));
+        UserResponse.LoginDTO loginDTO = userService.로그인(reqDTO);
+        return ResponseEntity.ok()
+                .header(JwtUtil.HEADER, loginDTO.accessToken())
+                .body(new Resp<>(200, "성공", loginDTO.user()));
     }
 
     @GetMapping("/check-username")
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
index 483c69d..db48a39 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
@@ -9,6 +9,9 @@ public class UserResponse {
         }
     }
 
+    public record LoginDTO(String accessToken, DTO user) {
+    }
+
     public record AvailableDTO(boolean available) {
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index fc07c5a..5e67aca 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.user;
 
 import com.newtenco.blog.core.handler.errors.BadRequestException;
 import com.newtenco.blog.core.handler.errors.UnauthorizedException;
+import com.newtenco.blog.core.util.JwtUtil;
 import lombok.RequiredArgsConstructor;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;
@@ -25,14 +26,14 @@ public class UserService {
         return new UserResponse.DTO(savedUser);
     }
 
-    public UserResponse.DTO 로그인(UserRequest.LoginDTO reqDTO) {
+    public UserResponse.LoginDTO 로그인(UserRequest.LoginDTO reqDTO) {
         User user = userRepository.findByUsername(reqDTO.username())
                 .orElseThrow(() -> new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다"));
 
         if (!passwordEncoder.matches(reqDTO.password(), user.getPassword())) {
             throw new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다");
         }
-        return new UserResponse.DTO(user);
+        return new UserResponse.LoginDTO(JwtUtil.create(user), new UserResponse.DTO(user));
     }
 
     public UserResponse.AvailableDTO 유저네임중복체크(String username) {
```

## T3 빈 값 검사하기
커밋: 0483deb

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index 93e41f0..9da0aa8 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -23,7 +23,7 @@ public class UserController {
     }
 
     @PostMapping("/login")
-    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO reqDTO) {
+    public ResponseEntity<?> login(@Valid @RequestBody UserRequest.LoginDTO reqDTO) {
         UserResponse.LoginDTO loginDTO = userService.로그인(reqDTO);
         return ResponseEntity.ok()
                 .header(JwtUtil.HEADER, loginDTO.accessToken())
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index e318582..a08d6fd 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -28,6 +28,11 @@ public class UserRequest {
         }
     }
 
-    public record LoginDTO(String username, String password) {
+    public record LoginDTO(
+            @NotBlank(message = "유저네임을 입력해주세요")
+            String username,
+
+            @NotBlank(message = "비밀번호를 입력해주세요")
+            String password) {
     }
 }
```

## T4 리액트에서 토큰 헤더 읽게 하기
커밋: 383b2c9

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |
| server-spring/src/main/resources/application.properties | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index c88faa3..29a22fe 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -1,24 +1,48 @@
 package com.newtenco.blog.core;
 
+import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
+import org.springframework.web.cors.CorsConfiguration;
+import org.springframework.web.cors.CorsConfigurationSource;
+import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
+
+import java.util.List;
 
 @Configuration
 public class SecurityConfig {
 
+    @Value("${app.cors.allowed-origins}")
+    private String allowedOrigins;
+
     @Bean
     public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
     }
 
+    @Bean
+    public CorsConfigurationSource corsConfigurationSource() {
+        CorsConfiguration config = new CorsConfiguration();
+        config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
+        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
+        config.addAllowedHeader("*");
+        config.setExposedHeaders(List.of("Authorization"));
+
+        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
+        source.registerCorsConfiguration("/**", config);
+        return source;
+    }
+
     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
 
+        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
+
         http.csrf(csrf -> csrf.disable());
         http.formLogin(form -> form.disable());
         http.httpBasic(basic -> basic.disable());
```

```diff
diff --git a/server-spring/src/main/resources/application.properties b/server-spring/src/main/resources/application.properties
index 06e2396..4e6f866 100644
--- a/server-spring/src/main/resources/application.properties
+++ b/server-spring/src/main/resources/application.properties
@@ -15,3 +15,5 @@ spring.jpa.open-in-view=false
 
 spring.sql.init.data-locations=classpath:db/data.sql
 spring.jpa.defer-datasource-initialization=true
+
+app.cors.allowed-origins=http://localhost:3000,http://localhost:5173,chrome-extension://*
```

