# board-write 코드 요약

## T1 글쓰기 API 만들기
커밋: 2c5ba56

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/Board.java b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
index 00a6ca0..54dfe1c 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/Board.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
@@ -9,6 +9,7 @@ import jakarta.persistence.GenerationType;
 import jakarta.persistence.Id;
 import jakarta.persistence.ManyToOne;
 import jakarta.persistence.Table;
+import lombok.Builder;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import org.hibernate.annotations.CreationTimestamp;
@@ -36,4 +37,13 @@ public class Board {
 
     @CreationTimestamp
     private Timestamp createdAt;
+
+    @Builder
+    public Board(Integer id, String title, String content, User user, Timestamp createdAt) {
+        this.id = id;
+        this.title = title;
+        this.content = content;
+        this.user = user;
+        this.createdAt = createdAt;
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index 4a849aa..c2457db 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -1,10 +1,14 @@
 package com.newtenco.blog.board;
 
 import com.newtenco.blog.core.util.Resp;
+import com.newtenco.blog.user.User;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
+import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
+import org.springframework.web.bind.annotation.PostMapping;
+import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 
@@ -20,6 +24,11 @@ public class BoardController {
         return Resp.ok(boardService.글목록(page, keyword));
     }
 
+    @PostMapping("/api/boards")
+    public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @RequestBody BoardRequest.SaveDTO reqDTO) {
+        return Resp.ok(boardService.글쓰기(reqDTO, sessionUser));
+    }
+
     @GetMapping("/api/boards/{id}")
     public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
         return Resp.ok(boardService.글상세(id));
```

```java
// server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
package com.newtenco.blog.board;

import com.newtenco.blog.user.User;

public class BoardRequest {

    public record SaveDTO(String title, String content) {

        public Board toEntity(User user) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .user(user)
                    .build();
        }
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 7bca8df..23846da 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -1,6 +1,7 @@
 package com.newtenco.blog.board;
 
 import com.newtenco.blog.core.handler.errors.NotFoundException;
+import com.newtenco.blog.user.User;
 import lombok.RequiredArgsConstructor;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;
@@ -20,6 +21,12 @@ public class BoardService {
         return new BoardResponse.PageDTO(boardRepository.findAllJoinUser(keyword, pageable));
     }
 
+    @Transactional
+    public BoardResponse.ListDTO 글쓰기(BoardRequest.SaveDTO reqDTO, User sessionUser) {
+        Board savedBoard = boardRepository.save(reqDTO.toEntity(sessionUser));
+        return new BoardResponse.ListDTO(savedBoard);
+    }
+
     public BoardResponse.DetailDTO 글상세(Integer id) {
         Board board = boardRepository.findByIdJoinUser(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
```

## T2 입력 검사하기
커밋: 3e747f9

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index c2457db..abc6881 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.board;
 
 import com.newtenco.blog.core.util.Resp;
 import com.newtenco.blog.user.User;
+import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
@@ -25,7 +26,7 @@ public class BoardController {
     }
 
     @PostMapping("/api/boards")
-    public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @RequestBody BoardRequest.SaveDTO reqDTO) {
+    public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @Valid @RequestBody BoardRequest.SaveDTO reqDTO) {
         return Resp.ok(boardService.글쓰기(reqDTO, sessionUser));
     }
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
index ee14228..34c3ad2 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
@@ -1,10 +1,19 @@
 package com.newtenco.blog.board;
 
 import com.newtenco.blog.user.User;
+import jakarta.validation.constraints.NotBlank;
+import jakarta.validation.constraints.Size;
 
 public class BoardRequest {
 
-    public record SaveDTO(String title, String content) {
+    public record SaveDTO(
+            @NotBlank(message = "제목을 입력해주세요")
+            @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
+            String title,
+
+            @NotBlank(message = "내용을 입력해주세요")
+            @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요")
+            String content) {
 
         public Board toEntity(User user) {
             return Board.builder()
```

