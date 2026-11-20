# board-owner 코드 요약

## T1 내 글 표시하기
커밋: f232157

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index 6beac07..8f021c0 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -50,7 +50,8 @@ public class BoardController {
     }
 
     @GetMapping("/api/boards/{id}")
-    public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
-        return Resp.ok(boardService.글상세(id));
+    public ResponseEntity<?> detail(@AuthenticationPrincipal User sessionUser, @PathVariable("id") Integer id) {
+        Integer sessionUserId = sessionUser == null ? null : sessionUser.getId();
+        return Resp.ok(boardService.글상세(id, sessionUserId));
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
index 379f1bc..0495774 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
@@ -26,11 +26,12 @@ public class BoardResponse {
         }
     }
 
-    public record DetailDTO(Integer id, String title, String content, Integer userId, String username,
+    public record DetailDTO(Integer id, String title, String content, Integer userId, String username, boolean owner,
                             List<ReplyDTO> replies) {
 
-        public DetailDTO(Board board) {
+        public DetailDTO(Board board, Integer sessionUserId) {
             this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getId(), board.getUser().getUsername(),
+                    board.getUser().getId().equals(sessionUserId),
                     board.getReplies().stream().map(ReplyDTO::new).toList());
         }
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 9aa8b51..52fe0a0 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -58,9 +58,9 @@ public class BoardService {
         boardRepository.delete(board);
     }
 
-    public BoardResponse.DetailDTO 글상세(Integer id) {
+    public BoardResponse.DetailDTO 글상세(Integer id, Integer sessionUserId) {
         Board board = boardRepository.findByIdJoinUserAndReplies(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
-        return new BoardResponse.DetailDTO(board);
+        return new BoardResponse.DetailDTO(board, sessionUserId);
     }
 }
```

## T2 내 댓글 표시하기
커밋: 017576a

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
index 0495774..dc4a039 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
@@ -32,13 +32,14 @@ public class BoardResponse {
         public DetailDTO(Board board, Integer sessionUserId) {
             this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getId(), board.getUser().getUsername(),
                     board.getUser().getId().equals(sessionUserId),
-                    board.getReplies().stream().map(ReplyDTO::new).toList());
+                    board.getReplies().stream().map(reply -> new ReplyDTO(reply, sessionUserId)).toList());
         }
 
-        public record ReplyDTO(Integer id, String comment, Integer userId, String username) {
+        public record ReplyDTO(Integer id, String comment, Integer userId, String username, boolean owner) {
 
-            public ReplyDTO(Reply reply) {
-                this(reply.getId(), reply.getComment(), reply.getUser().getId(), reply.getUser().getUsername());
+            public ReplyDTO(Reply reply, Integer sessionUserId) {
+                this(reply.getId(), reply.getComment(), reply.getUser().getId(), reply.getUser().getUsername(),
+                        reply.getUser().getId().equals(sessionUserId));
             }
         }
     }
```

## 테스트 보강
커밋: 36440c7
- 학생 코드 변경 없음
