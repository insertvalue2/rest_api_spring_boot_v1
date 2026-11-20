# admin-api 코드 요약

## T1 역할을 저장하고 토큰에 담기
커밋: 44b26ef

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 |
| server-spring/src/main/resources/db/data.sql | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java b/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
index 48d37b8..f2a45f5 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
@@ -6,6 +6,7 @@ import com.auth0.jwt.interfaces.DecodedJWT;
 import com.newtenco.blog.user.User;
 
 import java.util.Date;
+import java.util.List;
 
 public class JwtUtil {
 
@@ -22,6 +23,7 @@ public class JwtUtil {
                 .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                 .withClaim("id", user.getId())
                 .withClaim("username", user.getUsername())
+                .withClaim("roles", user.getRoles())
                 .sign(ALGORITHM);
         return TOKEN_PREFIX + jwt;
     }
@@ -30,6 +32,7 @@ public class JwtUtil {
         DecodedJWT decoded = JWT.require(ALGORITHM).build().verify(jwt);
         Integer id = decoded.getClaim("id").asInt();
         String username = decoded.getSubject();
-        return User.builder().id(id).username(username).build();
+        List<String> roles = decoded.getClaim("roles").asList(String.class);
+        return User.builder().id(id).username(username).roles(roles).build();
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/User.java b/server-spring/src/main/java/com/newtenco/blog/user/User.java
index 789b05e..5bd1f1e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/User.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/User.java
@@ -1,10 +1,14 @@
 package com.newtenco.blog.user;
 
+import jakarta.persistence.CollectionTable;
 import jakarta.persistence.Column;
+import jakarta.persistence.ElementCollection;
 import jakarta.persistence.Entity;
+import jakarta.persistence.FetchType;
 import jakarta.persistence.GeneratedValue;
 import jakarta.persistence.GenerationType;
 import jakarta.persistence.Id;
+import jakarta.persistence.JoinColumn;
 import jakarta.persistence.Table;
 import lombok.Builder;
 import lombok.Getter;
@@ -12,6 +16,8 @@ import lombok.NoArgsConstructor;
 import org.hibernate.annotations.CreationTimestamp;
 
 import java.sql.Timestamp;
+import java.util.ArrayList;
+import java.util.List;
 
 @NoArgsConstructor
 @Getter
@@ -32,15 +38,23 @@ public class User {
     @Column(length = 30, nullable = false)
     private String email;
 
+    @ElementCollection(fetch = FetchType.EAGER)
+    @CollectionTable(name = "user_role_tb", joinColumns = @JoinColumn(name = "user_id"))
+    @Column(name = "role")
+    private List<String> roles = new ArrayList<>(List.of("USER"));
+
     @CreationTimestamp
     private Timestamp createdAt;
 
     @Builder
-    public User(Integer id, String username, String password, String email, Timestamp createdAt) {
+    public User(Integer id, String username, String password, String email, List<String> roles, Timestamp createdAt) {
         this.id = id;
         this.username = username;
         this.password = password;
         this.email = email;
+        if (roles != null) {
+            this.roles = roles;
+        }
         this.createdAt = createdAt;
     }
 
```

```diff
diff --git a/server-spring/src/main/resources/db/data.sql b/server-spring/src/main/resources/db/data.sql
index b5ce189..5e8d89e 100644
--- a/server-spring/src/main/resources/db/data.sql
+++ b/server-spring/src/main/resources/db/data.sql
@@ -1,6 +1,10 @@
 insert into user_tb (username, password, email, created_at) values ('ssar', '$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO', 'ssar@metacoding.com', now());
 insert into user_tb (username, password, email, created_at) values ('cos', '$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO', 'cos@metacoding.com', now());
 
+insert into user_role_tb (user_id, role) values (1, 'USER');
+insert into user_role_tb (user_id, role) values (2, 'USER');
+insert into user_role_tb (user_id, role) values (2, 'ADMIN');
+
 insert into board_tb (title, content, user_id, created_at) values ('제목1', '내용1', 1, now());
 insert into board_tb (title, content, user_id, created_at) values ('제목2', '내용2', 1, now());
 insert into board_tb (title, content, user_id, created_at) values ('제목3', '내용3', 1, now());
```

## T2 관리자 전용 주소 막기
커밋: ee4d3b2

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 |

```java
// server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
package com.newtenco.blog.admin;

import com.newtenco.blog.core.util.Resp;
import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    @GetMapping("/api/admin/hello")
    public ResponseEntity<?> hello(@AuthenticationPrincipal User sessionUser) {
        return Resp.ok("관리자 " + sessionUser.getUsername() + "님 환영합니다");
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index fc79caf..ec2815f 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -64,9 +64,10 @@ public class SecurityConfig {
                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                 .requestMatchers("/join", "/login", "/check-username").permitAll()
                 .requestMatchers("/h2-console/**").permitAll()
+                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                 .requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()
                 .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
-                .requestMatchers("/api/**").authenticated()
+                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                 .anyRequest().denyAll()
         );
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java b/server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java
index 016298e..42d65d6 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java
@@ -13,7 +13,6 @@ import org.springframework.security.core.context.SecurityContextHolder;
 import org.springframework.web.filter.OncePerRequestFilter;
 
 import java.io.IOException;
-import java.util.List;
 
 public class JwtAuthorizationFilter extends OncePerRequestFilter {
 
@@ -26,7 +25,7 @@ public class JwtAuthorizationFilter extends OncePerRequestFilter {
         if (header != null && header.startsWith(JwtUtil.TOKEN_PREFIX)) {
             try {
                 User user = JwtUtil.verify(header.substring(JwtUtil.TOKEN_PREFIX.length()));
-                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
+                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                 SecurityContextHolder.getContext().setAuthentication(authentication);
             } catch (JWTVerificationException e) {
             }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/User.java b/server-spring/src/main/java/com/newtenco/blog/user/User.java
index 5bd1f1e..a962798 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/User.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/User.java
@@ -14,6 +14,7 @@ import lombok.Builder;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import org.hibernate.annotations.CreationTimestamp;
+import org.springframework.security.core.authority.SimpleGrantedAuthority;
 
 import java.sql.Timestamp;
 import java.util.ArrayList;
@@ -58,6 +59,12 @@ public class User {
         this.createdAt = createdAt;
     }
 
+    public List<SimpleGrantedAuthority> getAuthorities() {
+        return roles.stream()
+                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
+                .toList();
+    }
+
     public void update(String email, String password) {
         this.email = email;
         this.password = password;
```

## T3 관리자가 글 지우기
커밋: 1acf4ef

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java | 새로 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java b/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
index 624e442..e78ebe5 100644
--- a/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
@@ -5,15 +5,25 @@ import com.newtenco.blog.user.User;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
+import org.springframework.web.bind.annotation.DeleteMapping;
 import org.springframework.web.bind.annotation.GetMapping;
+import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.RestController;
 
 @RequiredArgsConstructor
 @RestController
 public class AdminController {
 
+    private final AdminService adminService;
+
     @GetMapping("/api/admin/hello")
     public ResponseEntity<?> hello(@AuthenticationPrincipal User sessionUser) {
         return Resp.ok("관리자 " + sessionUser.getUsername() + "님 환영합니다");
     }
+
+    @DeleteMapping("/api/admin/boards/{id}")
+    public ResponseEntity<?> deleteBoard(@PathVariable("id") Integer id) {
+        adminService.관리자_게시글삭제(id);
+        return Resp.ok(null);
+    }
 }
```

```java
// server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
package com.newtenco.blog.admin;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminService {

    private final BoardRepository boardRepository;

    @Transactional
    public void 관리자_게시글삭제(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        boardRepository.delete(board);
    }
}
```

## 테스트 보강
커밋: 78ef9fa
- 학생 코드 변경 없음
