# admin-roles 코드 요약

## T1 회원 역할 바꾸기
커밋: 6352eed

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java b/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
index e78ebe5..a31df08 100644
--- a/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
@@ -2,12 +2,15 @@ package com.newtenco.blog.admin;
 
 import com.newtenco.blog.core.util.Resp;
 import com.newtenco.blog.user.User;
+import com.newtenco.blog.user.UserRequest;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.DeleteMapping;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
+import org.springframework.web.bind.annotation.PutMapping;
+import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RestController;
 
 @RequiredArgsConstructor
@@ -26,4 +29,9 @@ public class AdminController {
         adminService.관리자_게시글삭제(id);
         return Resp.ok(null);
     }
+
+    @PutMapping("/api/admin/users/{id}")
+    public ResponseEntity<?> updateRoles(@PathVariable("id") Integer id, @RequestBody UserRequest.RolesDTO reqDTO) {
+        return Resp.ok(adminService.관리자_역할수정(id, reqDTO));
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java b/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
index a26b0dd..061c859 100644
--- a/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
@@ -3,6 +3,10 @@ package com.newtenco.blog.admin;
 import com.newtenco.blog.board.Board;
 import com.newtenco.blog.board.BoardRepository;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
+import com.newtenco.blog.user.User;
+import com.newtenco.blog.user.UserRepository;
+import com.newtenco.blog.user.UserRequest;
+import com.newtenco.blog.user.UserResponse;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
@@ -13,6 +17,7 @@ import org.springframework.transaction.annotation.Transactional;
 public class AdminService {
 
     private final BoardRepository boardRepository;
+    private final UserRepository userRepository;
 
     @Transactional
     public void 관리자_게시글삭제(Integer id) {
@@ -20,4 +25,12 @@ public class AdminService {
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
         boardRepository.delete(board);
     }
+
+    @Transactional
+    public UserResponse.RolesDTO 관리자_역할수정(Integer id, UserRequest.RolesDTO reqDTO) {
+        User user = userRepository.findById(id)
+                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
+        user.updateRoles(reqDTO.roles());
+        return new UserResponse.RolesDTO(user);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/User.java b/server-spring/src/main/java/com/newtenco/blog/user/User.java
index a962798..41b3ccd 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/User.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/User.java
@@ -69,4 +69,9 @@ public class User {
         this.email = email;
         this.password = password;
     }
+
+    public void updateRoles(List<String> roles) {
+        this.roles.clear();
+        this.roles.addAll(roles);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index c737d4d..4ab41cf 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -5,6 +5,8 @@ import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.Pattern;
 import jakarta.validation.constraints.Size;
 
+import java.util.List;
+
 public class UserRequest {
 
     public record JoinDTO(
@@ -49,4 +51,7 @@ public class UserRequest {
             return password != null && !password.isEmpty();
         }
     }
+
+    public record RolesDTO(List<String> roles) {
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
index db48a39..34887bb 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
@@ -1,5 +1,7 @@
 package com.newtenco.blog.user;
 
+import java.util.List;
+
 public class UserResponse {
 
     public record DTO(Integer id, String username, String email) {
@@ -14,4 +16,11 @@ public class UserResponse {
 
     public record AvailableDTO(boolean available) {
     }
+
+    public record RolesDTO(Integer id, String username, List<String> roles) {
+
+        public RolesDTO(User user) {
+            this(user.getId(), user.getUsername(), user.getRoles());
+        }
+    }
 }
```

## T2 역할 검사하기
커밋: 7900789

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java b/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
index a31df08..a20ef9e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
@@ -3,6 +3,7 @@ package com.newtenco.blog.admin;
 import com.newtenco.blog.core.util.Resp;
 import com.newtenco.blog.user.User;
 import com.newtenco.blog.user.UserRequest;
+import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
@@ -31,7 +32,7 @@ public class AdminController {
     }
 
     @PutMapping("/api/admin/users/{id}")
-    public ResponseEntity<?> updateRoles(@PathVariable("id") Integer id, @RequestBody UserRequest.RolesDTO reqDTO) {
+    public ResponseEntity<?> updateRoles(@PathVariable("id") Integer id, @Valid @RequestBody UserRequest.RolesDTO reqDTO) {
         return Resp.ok(adminService.관리자_역할수정(id, reqDTO));
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java b/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
index 061c859..84fbfee 100644
--- a/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.admin;
 
 import com.newtenco.blog.board.Board;
 import com.newtenco.blog.board.BoardRepository;
+import com.newtenco.blog.core.handler.errors.BadRequestException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.user.User;
 import com.newtenco.blog.user.UserRepository;
@@ -11,11 +12,18 @@ import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
+import java.util.HashSet;
+import java.util.LinkedHashSet;
+import java.util.List;
+import java.util.Set;
+
 @RequiredArgsConstructor
 @Transactional(readOnly = true)
 @Service
 public class AdminService {
 
+    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ADMIN");
+
     private final BoardRepository boardRepository;
     private final UserRepository userRepository;
 
@@ -30,7 +38,14 @@ public class AdminService {
     public UserResponse.RolesDTO 관리자_역할수정(Integer id, UserRequest.RolesDTO reqDTO) {
         User user = userRepository.findById(id)
                 .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
-        user.updateRoles(reqDTO.roles());
+        Set<String> requestedRoles = new LinkedHashSet<>(reqDTO.roles());
+        if (!ALLOWED_ROLES.containsAll(requestedRoles)) {
+            throw new BadRequestException("역할은 USER, ADMIN 만 사용할 수 있습니다");
+        }
+        if (new HashSet<>(user.getRoles()).equals(requestedRoles)) {
+            throw new BadRequestException("현재와 같은 역할입니다");
+        }
+        user.updateRoles(List.copyOf(requestedRoles));
         return new UserResponse.RolesDTO(user);
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
index 4ab41cf..7b9fee8 100644
--- a/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.user;
 
 import jakarta.validation.constraints.Email;
 import jakarta.validation.constraints.NotBlank;
+import jakarta.validation.constraints.NotEmpty;
 import jakarta.validation.constraints.Pattern;
 import jakarta.validation.constraints.Size;
 
@@ -52,6 +53,8 @@ public class UserRequest {
         }
     }
 
-    public record RolesDTO(List<String> roles) {
+    public record RolesDTO(
+            @NotEmpty(message = "역할을 설정해주세요")
+            List<String> roles) {
     }
 }
```

