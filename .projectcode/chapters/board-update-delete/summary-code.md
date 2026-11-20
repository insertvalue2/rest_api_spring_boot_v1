# board-update-delete 코드 요약

## T1 수정 화면 정보 보기
커밋: b2f0f4d

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index abc6881..0486270 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -30,6 +30,11 @@ public class BoardController {
         return Resp.ok(boardService.글쓰기(reqDTO, sessionUser));
     }
 
+    @GetMapping("/api/boards/{id}/edit")
+    public ResponseEntity<?> updateInfo(@PathVariable("id") Integer id) {
+        return Resp.ok(boardService.글수정정보(id));
+    }
+
     @GetMapping("/api/boards/{id}")
     public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
         return Resp.ok(boardService.글상세(id));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 23846da..d1b2710 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -27,6 +27,12 @@ public class BoardService {
         return new BoardResponse.ListDTO(savedBoard);
     }
 
+    public BoardResponse.ListDTO 글수정정보(Integer id) {
+        Board board = boardRepository.findById(id)
+                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
+        return new BoardResponse.ListDTO(board);
+    }
+
     public BoardResponse.DetailDTO 글상세(Integer id) {
         Board board = boardRepository.findByIdJoinUser(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
index dd32ec5..fc79caf 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
@@ -64,6 +64,7 @@ public class SecurityConfig {
                 .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                 .requestMatchers("/join", "/login", "/check-username").permitAll()
                 .requestMatchers("/h2-console/**").permitAll()
+                .requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()
                 .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                 .requestMatchers("/api/**").authenticated()
                 .anyRequest().denyAll()
```

## T2 작성자만 수정 정보 보게 막기
커밋: 121259b

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/ForbiddenException.java | 새로 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index 0486270..85ff216 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -31,8 +31,8 @@ public class BoardController {
     }
 
     @GetMapping("/api/boards/{id}/edit")
-    public ResponseEntity<?> updateInfo(@PathVariable("id") Integer id) {
-        return Resp.ok(boardService.글수정정보(id));
+    public ResponseEntity<?> updateInfo(@AuthenticationPrincipal User sessionUser, @PathVariable("id") Integer id) {
+        return Resp.ok(boardService.글수정정보(id, sessionUser.getId()));
     }
 
     @GetMapping("/api/boards/{id}")
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index d1b2710..b3642c7 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.board;
 
+import com.newtenco.blog.core.handler.errors.ForbiddenException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.user.User;
 import lombok.RequiredArgsConstructor;
@@ -27,9 +28,12 @@ public class BoardService {
         return new BoardResponse.ListDTO(savedBoard);
     }
 
-    public BoardResponse.ListDTO 글수정정보(Integer id) {
+    public BoardResponse.ListDTO 글수정정보(Integer id, Integer sessionUserId) {
         Board board = boardRepository.findById(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
+        if (!board.getUser().getId().equals(sessionUserId)) {
+            throw new ForbiddenException("게시글에 접근할 권한이 없습니다");
+        }
         return new BoardResponse.ListDTO(board);
     }
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index ca0334e..95ee9a6 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -1,6 +1,7 @@
 package com.newtenco.blog.core.handler;
 
 import com.newtenco.blog.core.handler.errors.BadRequestException;
+import com.newtenco.blog.core.handler.errors.ForbiddenException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.core.handler.errors.UnauthorizedException;
 import com.newtenco.blog.core.util.Resp;
@@ -39,6 +40,11 @@ public class GlobalExceptionHandler {
         return Resp.fail(HttpStatus.UNAUTHORIZED, e.getMessage());
     }
 
+    @ExceptionHandler(ForbiddenException.class)
+    public ResponseEntity<?> ex403(ForbiddenException e) {
+        return Resp.fail(HttpStatus.FORBIDDEN, e.getMessage());
+    }
+
     @ExceptionHandler(NotFoundException.class)
     public ResponseEntity<?> ex404(NotFoundException e) {
         return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/handler/errors/ForbiddenException.java
package com.newtenco.blog.core.handler.errors;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
```

## T3 글 수정하기
커밋: 29db1ad

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/Board.java b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
index 54dfe1c..eae622d 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/Board.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
@@ -46,4 +46,9 @@ public class Board {
         this.user = user;
         this.createdAt = createdAt;
     }
+
+    public void update(String title, String content) {
+        this.title = title;
+        this.content = content;
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index 85ff216..c9b0e15 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -9,6 +9,7 @@ import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
+import org.springframework.web.bind.annotation.PutMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
@@ -35,6 +36,12 @@ public class BoardController {
         return Resp.ok(boardService.글수정정보(id, sessionUser.getId()));
     }
 
+    @PutMapping("/api/boards/{id}")
+    public ResponseEntity<?> update(@AuthenticationPrincipal User sessionUser, @PathVariable("id") Integer id,
+                                    @Valid @RequestBody BoardRequest.UpdateDTO reqDTO) {
+        return Resp.ok(boardService.글수정(id, reqDTO, sessionUser.getId()));
+    }
+
     @GetMapping("/api/boards/{id}")
     public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
         return Resp.ok(boardService.글상세(id));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
index 34c3ad2..99de9fd 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
@@ -23,4 +23,14 @@ public class BoardRequest {
                     .build();
         }
     }
+
+    public record UpdateDTO(
+            @NotBlank(message = "제목을 입력해주세요")
+            @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
+            String title,
+
+            @NotBlank(message = "내용을 입력해주세요")
+            @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요")
+            String content) {
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index b3642c7..5485b2e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -37,6 +37,17 @@ public class BoardService {
         return new BoardResponse.ListDTO(board);
     }
 
+    @Transactional
+    public BoardResponse.ListDTO 글수정(Integer id, BoardRequest.UpdateDTO reqDTO, Integer sessionUserId) {
+        Board board = boardRepository.findById(id)
+                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
+        if (!board.getUser().getId().equals(sessionUserId)) {
+            throw new ForbiddenException("게시글을 수정할 권한이 없습니다");
+        }
+        board.update(reqDTO.title(), reqDTO.content());
+        return new BoardResponse.ListDTO(board);
+    }
+
     public BoardResponse.DetailDTO 글상세(Integer id) {
         Board board = boardRepository.findByIdJoinUser(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
```

## T4 글 삭제하기
커밋: 77c0666

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index c9b0e15..6beac07 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -6,6 +6,7 @@ import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
+import org.springframework.web.bind.annotation.DeleteMapping;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
@@ -42,6 +43,12 @@ public class BoardController {
         return Resp.ok(boardService.글수정(id, reqDTO, sessionUser.getId()));
     }
 
+    @DeleteMapping("/api/boards/{id}")
+    public ResponseEntity<?> delete(@AuthenticationPrincipal User sessionUser, @PathVariable("id") Integer id) {
+        boardService.글삭제(id, sessionUser.getId());
+        return Resp.ok(null);
+    }
+
     @GetMapping("/api/boards/{id}")
     public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
         return Resp.ok(boardService.글상세(id));
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 5485b2e..4c1e3b0 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -48,6 +48,16 @@ public class BoardService {
         return new BoardResponse.ListDTO(board);
     }
 
+    @Transactional
+    public void 글삭제(Integer id, Integer sessionUserId) {
+        Board board = boardRepository.findById(id)
+                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
+        if (!board.getUser().getId().equals(sessionUserId)) {
+            throw new ForbiddenException("게시글을 삭제할 권한이 없습니다");
+        }
+        boardRepository.delete(board);
+    }
+
     public BoardResponse.DetailDTO 글상세(Integer id) {
         Board board = boardRepository.findByIdJoinUser(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
```

