# user-update 코드 요약

## T1 이메일 수정하기
커밋: 10f05d4

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/User.java b/server-spring/src/main/java/com/newtenco/blog/user/User.java
index 8d95025..789b05e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/User.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/User.java
@@ -43,4 +43,9 @@ public class User {
         this.email = email;
         this.createdAt = createdAt;
     }
+
+    public void update(String email, String password) {
+        this.email = email;
+        this.password = password;
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index cc421fa..6b2b99e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -8,6 +8,7 @@ import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PostMapping;
+import org.springframework.web.bind.annotation.PutMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
@@ -36,6 +37,11 @@ public class UserController {
         return Resp.ok(userService.내정보(sessionUser.getId()));
     }
 
+    @PutMapping("/api/users/me")
+    public ResponseEntity<?> update(@AuthenticationPrincipal User sessionUser, @RequestBody UserRequest.UpdateDTO reqDTO) {
+        return Resp.ok(userService.회원수정(reqDTO, sessionUser.getId()));
+    }
+
     @GetMapping("/check-username")
     public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
         return Resp.ok(userService.유저네임중복체크(username));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index a08d6fd..8f612c2 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -35,4 +35,7 @@ public class UserRequest {
             @NotBlank(message = "비밀번호를 입력해주세요")
             String password) {
     }
+
+    public record UpdateDTO(String email, String password) {
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index a5bad84..80a1a07 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -43,6 +43,14 @@ public class UserService {
         return new UserResponse.DTO(user);
     }
 
+    @Transactional
+    public UserResponse.DTO 회원수정(UserRequest.UpdateDTO reqDTO, Integer sessionUserId) {
+        User user = userRepository.findById(sessionUserId)
+                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
+        user.update(reqDTO.email(), user.getPassword());
+        return new UserResponse.DTO(user);
+    }
+
     public UserResponse.AvailableDTO 유저네임중복체크(String username) {
         boolean available = !userRepository.existsByUsername(username);
         return new UserResponse.AvailableDTO(available);
```

## T2 비밀번호도 바꾸기
커밋: 1c3dbbe

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index 8f612c2..fcae91c 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -37,5 +37,9 @@ public class UserRequest {
     }
 
     public record UpdateDTO(String email, String password) {
+
+        public boolean isPasswordChange() {
+            return password != null && !password.isEmpty();
+        }
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
index 80a1a07..b7303ff 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserService.java
@@ -47,7 +47,10 @@ public class UserService {
     public UserResponse.DTO 회원수정(UserRequest.UpdateDTO reqDTO, Integer sessionUserId) {
         User user = userRepository.findById(sessionUserId)
                 .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
-        user.update(reqDTO.email(), user.getPassword());
+        String encPassword = reqDTO.isPasswordChange()
+                ? passwordEncoder.encode(reqDTO.password())
+                : user.getPassword();
+        user.update(reqDTO.email(), encPassword);
         return new UserResponse.DTO(user);
     }
 
```

## T3 입력 검사하기
커밋: 1435cab

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
index 6b2b99e..1985cfb 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserController.java
@@ -38,7 +38,7 @@ public class UserController {
     }
 
     @PutMapping("/api/users/me")
-    public ResponseEntity<?> update(@AuthenticationPrincipal User sessionUser, @RequestBody UserRequest.UpdateDTO reqDTO) {
+    public ResponseEntity<?> update(@AuthenticationPrincipal User sessionUser, @Valid @RequestBody UserRequest.UpdateDTO reqDTO) {
         return Resp.ok(userService.회원수정(reqDTO, sessionUser.getId()));
     }
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index fcae91c..c737d4d 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.user;
 
 import jakarta.validation.constraints.Email;
 import jakarta.validation.constraints.NotBlank;
+import jakarta.validation.constraints.Pattern;
 import jakarta.validation.constraints.Size;
 
 public class UserRequest {
@@ -36,7 +37,13 @@ public class UserRequest {
             String password) {
     }
 
-    public record UpdateDTO(String email, String password) {
+    public record UpdateDTO(
+            @NotBlank(message = "이메일을 입력해주세요")
+            @Email(message = "이메일 형식이 올바르지 않습니다")
+            String email,
+
+            @Pattern(regexp = "^$|^.{4,20}$", message = "비밀번호는 4~20자여야 합니다")
+            String password) {
 
         public boolean isPasswordChange() {
             return password != null && !password.isEmpty();
```

## 테스트 보강
커밋: b8eee9b
- 학생 코드 변경 없음
