# user-join 코드 요약

## T1 Security 넣고 모든 주소 열기
커밋: cdaa170

| 파일 | 변경 |
|---|---|
| server-spring/build.gradle | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 새로 |

```diff
diff --git a/server-spring/build.gradle b/server-spring/build.gradle
index bc59eb1..d4ab483 100644
--- a/server-spring/build.gradle
+++ b/server-spring/build.gradle
@@ -20,6 +20,7 @@ repositories {
 dependencies {
     implementation 'org.springframework.boot:spring-boot-h2console'
     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
+    implementation 'org.springframework.boot:spring-boot-starter-security'
     implementation 'org.springframework.boot:spring-boot-starter-webmvc'
     compileOnly 'org.projectlombok:lombok'
     developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
package com.newtenco.blog.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
```

## T2 회원가입 API 만들기
커밋: 1060d34

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 새로 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index a61e5de..c88faa3 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -3,11 +3,18 @@ package com.newtenco.blog.core;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
+import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
+import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
 
 @Configuration
 public class SecurityConfig {
 
+    @Bean
+    public PasswordEncoder passwordEncoder() {
+        return new BCryptPasswordEncoder();
+    }
+
     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/User.java b/server-spring/src/main/java/com/newtenco/blog/user/User.java
index c961a6f..8d95025 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/User.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/User.java
@@ -6,6 +6,7 @@ import jakarta.persistence.GeneratedValue;
 import jakarta.persistence.GenerationType;
 import jakarta.persistence.Id;
 import jakarta.persistence.Table;
+import lombok.Builder;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import org.hibernate.annotations.CreationTimestamp;
@@ -33,4 +34,13 @@ public class User {
 
     @CreationTimestamp
     private Timestamp createdAt;
+
+    @Builder
+    public User(Integer id, String username, String password, String email, Timestamp createdAt) {
+        this.id = id;
+        this.username = username;
+        this.password = password;
+        this.email = email;
+        this.createdAt = createdAt;
+    }
 }
```

```java
// server-spring/src/main/java/com/newtenco/blog/user/UserController.java
package com.newtenco.blog.user;

import com.newtenco.blog.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserRequest.JoinDTO reqDTO) {
        return Resp.ok(userService.회원가입(reqDTO));
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
package com.newtenco.blog.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
package com.newtenco.blog.user;

public class UserRequest {

    public record JoinDTO(String username, String password, String email) {

        public User toEntity(String encPassword) {
            return User.builder()
                    .username(username)
                    .password(encPassword)
                    .email(email)
                    .build();
        }
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
package com.newtenco.blog.user;

public class UserResponse {

    public record DTO(Integer id, String username, String email) {

        public DTO(User user) {
            this(user.getId(), user.getUsername(), user.getEmail());
        }
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/user/UserService.java
package com.newtenco.blog.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse.DTO 회원가입(UserRequest.JoinDTO reqDTO) {
        String encPassword = passwordEncoder.encode(reqDTO.password());
        User savedUser = userRepository.save(reqDTO.toEntity(encPassword));
        return new UserResponse.DTO(savedUser);
    }
}
```

## T3 입력 검사하기
커밋: 09c8f54

| 파일 | 변경 |
|---|---|
| server-spring/build.gradle | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |

```diff
diff --git a/server-spring/build.gradle b/server-spring/build.gradle
index d4ab483..b4e5e73 100644
--- a/server-spring/build.gradle
+++ b/server-spring/build.gradle
@@ -21,6 +21,7 @@ dependencies {
     implementation 'org.springframework.boot:spring-boot-h2console'
     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
     implementation 'org.springframework.boot:spring-boot-starter-security'
+    implementation 'org.springframework.boot:spring-boot-starter-validation'
     implementation 'org.springframework.boot:spring-boot-starter-webmvc'
     compileOnly 'org.projectlombok:lombok'
     developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index d7f043b..bf71beb 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -5,7 +5,9 @@ import com.newtenco.blog.core.util.Resp;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
+import org.springframework.validation.FieldError;
 import org.springframework.web.ErrorResponse;
+import org.springframework.web.bind.MethodArgumentNotValidException;
 import org.springframework.web.bind.annotation.ExceptionHandler;
 import org.springframework.web.bind.annotation.RestControllerAdvice;
 
@@ -13,6 +15,12 @@ import org.springframework.web.bind.annotation.RestControllerAdvice;
 @RestControllerAdvice
 public class GlobalExceptionHandler {
 
+    @ExceptionHandler(MethodArgumentNotValidException.class)
+    public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
+        FieldError fe = e.getBindingResult().getFieldErrors().getFirst();
+        return Resp.fail(HttpStatus.BAD_REQUEST, fe.getDefaultMessage());
+    }
+
     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<?> ex404(NotFoundException e) {
         return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index ad34e62..99fb78e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -1,6 +1,7 @@
 package com.newtenco.blog.user;
 
 import com.newtenco.blog.core.util.Resp;
+import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.PostMapping;
@@ -14,7 +15,7 @@ public class UserController {
     private final UserService userService;
 
     @PostMapping("/join")
-    public ResponseEntity<?> join(@RequestBody UserRequest.JoinDTO reqDTO) {
+    public ResponseEntity<?> join(@Valid @RequestBody UserRequest.JoinDTO reqDTO) {
         return Resp.ok(userService.회원가입(reqDTO));
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index 55bbcfc..5d5fa01 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -1,8 +1,23 @@
 package com.newtenco.blog.user;
 
+import jakarta.validation.constraints.Email;
+import jakarta.validation.constraints.NotBlank;
+import jakarta.validation.constraints.Size;
+
 public class UserRequest {
 
-    public record JoinDTO(String username, String password, String email) {
+    public record JoinDTO(
+            @NotBlank(message = "유저네임을 입력해주세요")
+            @Size(min = 4, max = 20, message = "유저네임은 4~20자여야 합니다")
+            String username,
+
+            @NotBlank(message = "비밀번호를 입력해주세요")
+            @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
+            String password,
+
+            @NotBlank(message = "이메일을 입력해주세요")
+            @Email(message = "이메일 형식이 올바르지 않습니다")
+            String email) {
 
         public User toEntity(String encPassword) {
             return User.builder()
```

## T4 같은 아이디 막기
커밋: dbb6ff5

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/BadRequestException.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index bf71beb..129bd18 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.core.handler;
 
+import com.newtenco.blog.core.handler.errors.BadRequestException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.core.util.Resp;
 import lombok.extern.slf4j.Slf4j;
@@ -21,6 +22,11 @@ public class GlobalExceptionHandler {
         return Resp.fail(HttpStatus.BAD_REQUEST, fe.getDefaultMessage());
     }
 
+    @ExceptionHandler(BadRequestException.class)
+    public ResponseEntity<?> ex400(BadRequestException e) {
+        return Resp.fail(HttpStatus.BAD_REQUEST, e.getMessage());
+    }
+
     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<?> ex404(NotFoundException e) {
         return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/handler/errors/BadRequestException.java
package com.newtenco.blog.core.handler.errors;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
index faf8550..608e8b8 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
@@ -3,4 +3,6 @@ package com.newtenco.blog.user;
 import org.springframework.data.jpa.repository.JpaRepository;
 
 public interface UserRepository extends JpaRepository<User, Integer> {
+
+    boolean existsByUsername(String username);
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index e197ea1..11cdf0c 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.user;
 
+import com.newtenco.blog.core.handler.errors.BadRequestException;
 import lombok.RequiredArgsConstructor;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;
@@ -15,6 +16,9 @@ public class UserService {
 
     @Transactional
     public UserResponse.DTO 회원가입(UserRequest.JoinDTO reqDTO) {
+        if (userRepository.existsByUsername(reqDTO.username())) {
+            throw new BadRequestException("이미 존재하는 유저네임입니다");
+        }
         String encPassword = passwordEncoder.encode(reqDTO.password());
         User savedUser = userRepository.save(reqDTO.toEntity(encPassword));
         return new UserResponse.DTO(savedUser);
```

## T5 아이디 중복 확인 API
커밋: 5e4e8e6

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index 99fb78e..f14a36b 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -4,8 +4,10 @@ import com.newtenco.blog.core.util.Resp;
 import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
+import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
+import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 
 @RequiredArgsConstructor
@@ -18,4 +20,9 @@ public class UserController {
     public ResponseEntity<?> join(@Valid @RequestBody UserRequest.JoinDTO reqDTO) {
         return Resp.ok(userService.회원가입(reqDTO));
     }
+
+    @GetMapping("/check-username")
+    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
+        return Resp.ok(userService.유저네임중복체크(username));
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
index 274960e..483c69d 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
@@ -8,4 +8,7 @@ public class UserResponse {
             this(user.getId(), user.getUsername(), user.getEmail());
         }
     }
+
+    public record AvailableDTO(boolean available) {
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index 11cdf0c..7c0f81d 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -23,4 +23,9 @@ public class UserService {
         User savedUser = userRepository.save(reqDTO.toEntity(encPassword));
         return new UserResponse.DTO(savedUser);
     }
+
+    public UserResponse.AvailableDTO 유저네임중복체크(String username) {
+        boolean available = !userRepository.existsByUsername(username);
+        return new UserResponse.AvailableDTO(available);
+    }
 }
```

## T6 요청 값이 빠지면 400으로 알리기
커밋: 561c124

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index 129bd18..97bebe1 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -9,6 +9,7 @@ import org.springframework.http.ResponseEntity;
 import org.springframework.validation.FieldError;
 import org.springframework.web.ErrorResponse;
 import org.springframework.web.bind.MethodArgumentNotValidException;
+import org.springframework.web.bind.MissingServletRequestParameterException;
 import org.springframework.web.bind.annotation.ExceptionHandler;
 import org.springframework.web.bind.annotation.RestControllerAdvice;
 
@@ -22,6 +23,11 @@ public class GlobalExceptionHandler {
         return Resp.fail(HttpStatus.BAD_REQUEST, fe.getDefaultMessage());
     }
 
+    @ExceptionHandler(MissingServletRequestParameterException.class)
+    public ResponseEntity<?> missingParam(MissingServletRequestParameterException e) {
+        return Resp.fail(HttpStatus.BAD_REQUEST, e.getParameterName() + ":값을 입력해주세요");
+    }
+
     @ExceptionHandler(BadRequestException.class)
     public ResponseEntity<?> ex400(BadRequestException e) {
         return Resp.fail(HttpStatus.BAD_REQUEST, e.getMessage());
```

## 테스트 보강
커밋: ad195d7
- 학생 코드 변경 없음
