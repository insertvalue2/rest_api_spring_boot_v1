# refresh-token 코드 요약

## T1 로그인 때 리프레시 토큰 주기
커밋: a200bf8

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```java
// server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java
package com.newtenco.blog.auth;

import com.newtenco.blog.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "refresh_token_tb")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 100, nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Timestamp expiresAt;

    @Builder
    public RefreshToken(String token, User user, Timestamp expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
package com.newtenco.blog.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
package com.newtenco.blog.auth;

import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RefreshTokenService {

    public static final String HEADER = "Refresh-Token";
    public static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 14;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String 발급(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(new Timestamp(System.currentTimeMillis() + EXPIRATION_TIME))
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index ec2815f..2e40584 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -35,7 +35,7 @@ public class SecurityConfig {
         config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
         config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
         config.addAllowedHeader("*");
-        config.setExposedHeaders(List.of("Authorization"));
+        config.setExposedHeaders(List.of("Authorization", "Refresh-Token"));
 
         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         source.registerCorsConfiguration("/**", config);
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index 1985cfb..31ff467 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.user;
 
+import com.newtenco.blog.auth.RefreshTokenService;
 import com.newtenco.blog.core.util.JwtUtil;
 import com.newtenco.blog.core.util.Resp;
 import jakarta.validation.Valid;
@@ -29,6 +30,7 @@ public class UserController {
         UserResponse.LoginDTO loginDTO = userService.로그인(reqDTO);
         return ResponseEntity.ok()
                 .header(JwtUtil.HEADER, loginDTO.accessToken())
+                .header(RefreshTokenService.HEADER, loginDTO.refreshToken())
                 .body(new Resp<>(200, "성공", loginDTO.user()));
     }
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
index 34887bb..d1377a7 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
@@ -11,7 +11,7 @@ public class UserResponse {
         }
     }
 
-    public record LoginDTO(String accessToken, DTO user) {
+    public record LoginDTO(String accessToken, String refreshToken, DTO user) {
     }
 
     public record AvailableDTO(boolean available) {
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index b7303ff..3796432 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.user;
 
+import com.newtenco.blog.auth.RefreshTokenService;
 import com.newtenco.blog.core.handler.errors.BadRequestException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.core.handler.errors.UnauthorizedException;
@@ -16,6 +17,7 @@ public class UserService {
 
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
+    private final RefreshTokenService refreshTokenService;
 
     @Transactional
     public UserResponse.DTO 회원가입(UserRequest.JoinDTO reqDTO) {
@@ -27,6 +29,7 @@ public class UserService {
         return new UserResponse.DTO(savedUser);
     }
 
+    @Transactional
     public UserResponse.LoginDTO 로그인(UserRequest.LoginDTO reqDTO) {
         User user = userRepository.findByUsername(reqDTO.username())
                 .orElseThrow(() -> new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다"));
@@ -34,7 +37,8 @@ public class UserService {
         if (!passwordEncoder.matches(reqDTO.password(), user.getPassword())) {
             throw new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다");
         }
-        return new UserResponse.LoginDTO(JwtUtil.create(user), new UserResponse.DTO(user));
+        String refreshToken = refreshTokenService.발급(user);
+        return new UserResponse.LoginDTO(JwtUtil.create(user), refreshToken, new UserResponse.DTO(user));
     }
 
     public UserResponse.DTO 내정보(Integer id) {
```

## T2 리프레시 토큰으로 재발급하기
커밋: d1d0643

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/auth/AuthRequest.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/auth/AuthResponse.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |

```java
// server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java
package com.newtenco.blog.auth;

import com.newtenco.blog.core.util.JwtUtil;
import com.newtenco.blog.core.util.Resp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Valid @RequestBody AuthRequest.RefreshDTO reqDTO) {
        AuthResponse.ReissueDTO reissueDTO = refreshTokenService.재발급(reqDTO.refreshToken());
        return ResponseEntity.ok()
                .header(JwtUtil.HEADER, reissueDTO.accessToken())
                .header(RefreshTokenService.HEADER, reissueDTO.refreshToken())
                .body(new Resp<>(200, "성공", null));
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/auth/AuthRequest.java
package com.newtenco.blog.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    public record RefreshDTO(
            @NotBlank(message = "리프레시 토큰이 필요합니다")
            String refreshToken) {
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/auth/AuthResponse.java
package com.newtenco.blog.auth;

public class AuthResponse {

    public record ReissueDTO(String accessToken, String refreshToken) {
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java
index 3786516..43e4449 100644
--- a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java
+++ b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java
@@ -40,4 +40,8 @@ public class RefreshToken {
         this.user = user;
         this.expiresAt = expiresAt;
     }
+
+    public boolean isExpired() {
+        return expiresAt.before(new Timestamp(System.currentTimeMillis()));
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
index b8f7c43..2357cfa 100644
--- a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
@@ -1,6 +1,13 @@
 package com.newtenco.blog.auth;
 
 import org.springframework.data.jpa.repository.JpaRepository;
+import org.springframework.data.jpa.repository.Query;
+import org.springframework.data.repository.query.Param;
+
+import java.util.Optional;
 
 public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
+
+    @Query("select rt from RefreshToken rt join fetch rt.user where rt.token = :token")
+    Optional<RefreshToken> findByTokenJoinUser(@Param("token") String token);
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
index cb9e0a0..bc54f9f 100644
--- a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
@@ -1,5 +1,7 @@
 package com.newtenco.blog.auth;
 
+import com.newtenco.blog.core.handler.errors.UnauthorizedException;
+import com.newtenco.blog.core.util.JwtUtil;
 import com.newtenco.blog.user.User;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
@@ -28,4 +30,19 @@ public class RefreshTokenService {
         refreshTokenRepository.save(refreshToken);
         return refreshToken.getToken();
     }
+
+    @Transactional
+    public AuthResponse.ReissueDTO 재발급(String token) {
+        RefreshToken refreshToken = refreshTokenRepository.findByTokenJoinUser(token)
+                .orElseThrow(() -> new UnauthorizedException("다시 로그인해주세요"));
+        if (refreshToken.isExpired()) {
+            throw new UnauthorizedException("다시 로그인해주세요");
+        }
+        refreshTokenRepository.delete(refreshToken);
+
+        User user = refreshToken.getUser();
+        String newAccessToken = JwtUtil.create(user);
+        String newRefreshToken = 발급(user);
+        return new AuthResponse.ReissueDTO(newAccessToken, newRefreshToken);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index 2e40584..eb301d6 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -63,6 +63,7 @@ public class SecurityConfig {
         http.authorizeHttpRequests(auth -> auth
                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                 .requestMatchers("/join", "/login", "/check-username").permitAll()
+                .requestMatchers(HttpMethod.POST, "/reissue").permitAll()
                 .requestMatchers("/h2-console/**").permitAll()
                 .requestMatchers("/api/admin/**").hasRole("ADMIN")
                 .requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()
```

## T3 로그아웃하기
커밋: 2343ab0

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java b/server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java
index 7be1c80..86b4592 100644
--- a/server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java
@@ -23,4 +23,10 @@ public class AuthController {
                 .header(RefreshTokenService.HEADER, reissueDTO.refreshToken())
                 .body(new Resp<>(200, "성공", null));
     }
+
+    @PostMapping("/logout")
+    public ResponseEntity<?> logout(@Valid @RequestBody AuthRequest.RefreshDTO reqDTO) {
+        refreshTokenService.폐기(reqDTO.refreshToken());
+        return Resp.ok(null);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
index 2357cfa..ea7589b 100644
--- a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java
@@ -10,4 +10,6 @@ public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Inte
 
     @Query("select rt from RefreshToken rt join fetch rt.user where rt.token = :token")
     Optional<RefreshToken> findByTokenJoinUser(@Param("token") String token);
+
+    Optional<RefreshToken> findByToken(String token);
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
index bc54f9f..c785107 100644
--- a/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java
@@ -45,4 +45,9 @@ public class RefreshTokenService {
         String newRefreshToken = 발급(user);
         return new AuthResponse.ReissueDTO(newAccessToken, newRefreshToken);
     }
+
+    @Transactional
+    public void 폐기(String token) {
+        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index eb301d6..4f97b3b 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -51,6 +51,7 @@ public class SecurityConfig {
         http.csrf(csrf -> csrf.disable());
         http.formLogin(form -> form.disable());
         http.httpBasic(basic -> basic.disable());
+        http.logout(logout -> logout.disable());
 
         http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
 
@@ -63,7 +64,7 @@ public class SecurityConfig {
         http.authorizeHttpRequests(auth -> auth
                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                 .requestMatchers("/join", "/login", "/check-username").permitAll()
-                .requestMatchers(HttpMethod.POST, "/reissue").permitAll()
+                .requestMatchers(HttpMethod.POST, "/reissue", "/logout").permitAll()
                 .requestMatchers("/h2-console/**").permitAll()
                 .requestMatchers("/api/admin/**").hasRole("ADMIN")
                 .requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()
```

