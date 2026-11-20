# board-paging 코드 요약

## T1 목록을 3개씩 나누기
커밋: 5817c74

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
index 996472b..dca4cd6 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
@@ -1,16 +1,18 @@
 package com.newtenco.blog.board;
 
+import org.springframework.data.domain.Page;
+import org.springframework.data.domain.Pageable;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.data.repository.query.Param;
 
-import java.util.List;
 import java.util.Optional;
 
 public interface BoardRepository extends JpaRepository<Board, Integer> {
 
-    @Query("select b from Board b join fetch b.user order by b.id desc")
-    List<Board> findAllJoinUser();
+    @Query(value = "select b from Board b join fetch b.user",
+            countQuery = "select count(b) from Board b")
+    Page<Board> findAllJoinUser(Pageable pageable);
 
     @Query("select b from Board b join fetch b.user where b.id = :id")
     Optional<Board> findByIdJoinUser(@Param("id") Integer id);
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 6676479..25a4e52 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -2,6 +2,9 @@ package com.newtenco.blog.board;
 
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import lombok.RequiredArgsConstructor;
+import org.springframework.data.domain.PageRequest;
+import org.springframework.data.domain.Pageable;
+import org.springframework.data.domain.Sort;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
@@ -14,8 +17,9 @@ public class BoardService {
 
     private final BoardRepository boardRepository;
 
-    public List<BoardResponse.ListDTO> 글목록() {
-        return boardRepository.findAllJoinUser().stream()
+    public List<BoardResponse.ListDTO> 글목록(Integer page) {
+        Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
+        return boardRepository.findAllJoinUser(pageable).getContent().stream()
                 .map(BoardResponse.ListDTO::new)
                 .toList();
     }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index dd4a1d1..230e45f 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -5,6 +5,7 @@ import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
+import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 
 @RequiredArgsConstructor
@@ -14,8 +15,8 @@ public class BoardController {
     private final BoardService boardService;
 
     @GetMapping("/api/boards")
-    public ResponseEntity<?> list() {
-        return Resp.ok(boardService.글목록());
+    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") Integer page) {
+        return Resp.ok(boardService.글목록(page));
     }
 
     @GetMapping("/api/boards/{id}")
```

## T2 쪽 정보 함께 주기
커밋: 76404c9

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
index a0e422d..35f6e25 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
@@ -1,6 +1,9 @@
 package com.newtenco.blog.board;
 
+import org.springframework.data.domain.Page;
+
 import java.sql.Timestamp;
+import java.util.List;
 
 public class BoardResponse {
 
@@ -11,6 +14,17 @@ public class BoardResponse {
         }
     }
 
+    public record PageDTO(List<ListDTO> boards, int number, int totalPage, boolean isFirst, boolean isLast) {
+
+        public PageDTO(Page<Board> boardPage) {
+            this(boardPage.getContent().stream().map(ListDTO::new).toList(),
+                    boardPage.getNumber(),
+                    boardPage.getTotalPages(),
+                    boardPage.isFirst(),
+                    boardPage.isLast());
+        }
+    }
+
     public record DetailDTO(Integer id, String title, String content, Integer userId, String username) {
 
         public DetailDTO(Board board) {
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 25a4e52..4e813a6 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -8,8 +8,6 @@ import org.springframework.data.domain.Sort;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
-import java.util.List;
-
 @RequiredArgsConstructor
 @Transactional(readOnly = true)
 @Service
@@ -17,11 +15,9 @@ public class BoardService {
 
     private final BoardRepository boardRepository;
 
-    public List<BoardResponse.ListDTO> 글목록(Integer page) {
+    public BoardResponse.PageDTO 글목록(Integer page) {
         Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
-        return boardRepository.findAllJoinUser(pageable).getContent().stream()
-                .map(BoardResponse.ListDTO::new)
-                .toList();
+        return new BoardResponse.PageDTO(boardRepository.findAllJoinUser(pageable));
     }
 
     public BoardResponse.DetailDTO 글상세(Integer id) {
```

## T3 제목으로 검색하기
커밋: 518355d

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
index dca4cd6..d5fc0ee 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
@@ -10,9 +10,9 @@ import java.util.Optional;
 
 public interface BoardRepository extends JpaRepository<Board, Integer> {
 
-    @Query(value = "select b from Board b join fetch b.user",
-            countQuery = "select count(b) from Board b")
-    Page<Board> findAllJoinUser(Pageable pageable);
+    @Query(value = "select b from Board b join fetch b.user where b.title like concat('%', :keyword, '%')",
+            countQuery = "select count(b) from Board b where b.title like concat('%', :keyword, '%')")
+    Page<Board> findAllJoinUser(@Param("keyword") String keyword, Pageable pageable);
 
     @Query("select b from Board b join fetch b.user where b.id = :id")
     Optional<Board> findByIdJoinUser(@Param("id") Integer id);
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 4e813a6..7bca8df 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -15,9 +15,9 @@ public class BoardService {
 
     private final BoardRepository boardRepository;
 
-    public BoardResponse.PageDTO 글목록(Integer page) {
+    public BoardResponse.PageDTO 글목록(Integer page, String keyword) {
         Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
-        return new BoardResponse.PageDTO(boardRepository.findAllJoinUser(pageable));
+        return new BoardResponse.PageDTO(boardRepository.findAllJoinUser(keyword, pageable));
     }
 
     public BoardResponse.DetailDTO 글상세(Integer id) {
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index 230e45f..4a849aa 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -15,8 +15,9 @@ public class BoardController {
     private final BoardService boardService;
 
     @GetMapping("/api/boards")
-    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") Integer page) {
-        return Resp.ok(boardService.글목록(page));
+    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
+                                  @RequestParam(value = "keyword", defaultValue = "") String keyword) {
+        return Resp.ok(boardService.글목록(page, keyword));
     }
 
     @GetMapping("/api/boards/{id}")
```

