# board-detail 코드 요약

## T1 글 상세 API 만들기
커밋: 8b2a572

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
index cc2791e..996472b 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
@@ -2,11 +2,16 @@ package com.newtenco.blog.board;
 
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.Query;
+import org.springframework.data.repository.query.Param;
 
 import java.util.List;
+import java.util.Optional;
 
 public interface BoardRepository extends JpaRepository<Board, Integer> {
 
     @Query("select b from Board b join fetch b.user order by b.id desc")
     List<Board> findAllJoinUser();
+
+    @Query("select b from Board b join fetch b.user where b.id = :id")
+    Optional<Board> findByIdJoinUser(@Param("id") Integer id);
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
index 5d01a80..a0e422d 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
@@ -10,4 +10,11 @@ public class BoardResponse {
             this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getUsername(), board.getCreatedAt());
         }
     }
+
+    public record DetailDTO(Integer id, String title, String content, Integer userId, String username) {
+
+        public DetailDTO(Board board) {
+            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getId(), board.getUser().getUsername());
+        }
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 6d7d0d3..66364c6 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -18,4 +18,9 @@ public class BoardService {
                 .map(BoardResponse.ListDTO::new)
                 .toList();
     }
+
+    public BoardResponse.DetailDTO 글상세(Integer id) {
+        Board board = boardRepository.findByIdJoinUser(id).orElseThrow();
+        return new BoardResponse.DetailDTO(board);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index de350d2..dd4a1d1 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -4,6 +4,7 @@ import com.newtenco.blog.core.util.Resp;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.GetMapping;
+import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.RestController;
 
 @RequiredArgsConstructor
@@ -16,4 +17,9 @@ public class BoardController {
     public ResponseEntity<?> list() {
         return Resp.ok(boardService.글목록());
     }
+
+    @GetMapping("/api/boards/{id}")
+    public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
+        return Resp.ok(boardService.글상세(id));
+    }
 }
```

## T2 없는 글은 404로 알려주기
커밋: 6c6d77d

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/NotFoundException.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |

```java
// server-spring/src/main/java/com/newtenco/blog/core/handler/errors/NotFoundException.java
package com.newtenco.blog.core.handler.errors;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
package com.newtenco.blog.core.handler;

import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.core.util.Resp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> ex404(NotFoundException e) {
        return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java b/server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java
index 7c592ee..7db9fe3 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.core.util;
 
+import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 
 public record Resp<T>(Integer status, String msg, T body) {
@@ -7,4 +8,8 @@ public record Resp<T>(Integer status, String msg, T body) {
     public static <B> ResponseEntity<Resp<B>> ok(B body) {
         return ResponseEntity.ok(new Resp<>(200, "성공", body));
     }
+
+    public static ResponseEntity<Resp<Void>> fail(HttpStatus status, String msg) {
+        return ResponseEntity.status(status).body(new Resp<>(status.value(), msg, null));
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 66364c6..6676479 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.board;
 
+import com.newtenco.blog.core.handler.errors.NotFoundException;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
@@ -20,7 +21,8 @@ public class BoardService {
     }
 
     public BoardResponse.DetailDTO 글상세(Integer id) {
-        Board board = boardRepository.findByIdJoinUser(id).orElseThrow();
+        Board board = boardRepository.findByIdJoinUser(id)
+                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
         return new BoardResponse.DetailDTO(board);
     }
 }
```

## T3 처리하지 못한 오류 감싸기
커밋: d806b29

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index bd7dfa0..c9f04ad 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -2,11 +2,13 @@ package com.newtenco.blog.core.handler;
 
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.core.util.Resp;
+import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.ExceptionHandler;
 import org.springframework.web.bind.annotation.RestControllerAdvice;
 
+@Slf4j
 @RestControllerAdvice
 public class GlobalExceptionHandler {
 
@@ -14,4 +16,10 @@ public class GlobalExceptionHandler {
     public ResponseEntity<?> ex404(NotFoundException e) {
         return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
     }
+
+    @ExceptionHandler(Exception.class)
+    public ResponseEntity<?> exUnknown(Exception e) {
+        log.error("예상하지 못한 오류", e);
+        return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의하세요");
+    }
 }
```

## T4 스프링이 아는 오류는 원래 상태 지키기
커밋: ac823a9

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
index c9f04ad..d7f043b 100644
--- a/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
+++ b/server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
@@ -5,6 +5,7 @@ import com.newtenco.blog.core.util.Resp;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
+import org.springframework.web.ErrorResponse;
 import org.springframework.web.bind.annotation.ExceptionHandler;
 import org.springframework.web.bind.annotation.RestControllerAdvice;
 
@@ -19,6 +20,10 @@ public class GlobalExceptionHandler {
 
     @ExceptionHandler(Exception.class)
     public ResponseEntity<?> exUnknown(Exception e) {
+        if (e instanceof ErrorResponse er) {
+            HttpStatus status = HttpStatus.valueOf(er.getStatusCode().value());
+            return Resp.fail(status, status.getReasonPhrase());
+        }
         log.error("예상하지 못한 오류", e);
         return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의하세요");
     }
```

