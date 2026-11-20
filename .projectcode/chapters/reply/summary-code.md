# reply 코드 요약

## T1 글 상세에 댓글 함께 주기
커밋: 4ef779e

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/reply/Reply.java | 새로 |
| server-spring/src/main/resources/db/data.sql | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/Board.java b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
index eae622d..a33a7c5 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/Board.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.board;
 
+import com.newtenco.blog.reply.Reply;
 import com.newtenco.blog.user.User;
 import jakarta.persistence.Column;
 import jakarta.persistence.Entity;
@@ -8,6 +9,7 @@ import jakarta.persistence.GeneratedValue;
 import jakarta.persistence.GenerationType;
 import jakarta.persistence.Id;
 import jakarta.persistence.ManyToOne;
+import jakarta.persistence.OneToMany;
 import jakarta.persistence.Table;
 import lombok.Builder;
 import lombok.Getter;
@@ -15,6 +17,8 @@ import lombok.NoArgsConstructor;
 import org.hibernate.annotations.CreationTimestamp;
 
 import java.sql.Timestamp;
+import java.util.ArrayList;
+import java.util.List;
 
 @NoArgsConstructor
 @Getter
@@ -35,6 +39,9 @@ public class Board {
     @ManyToOne(fetch = FetchType.LAZY)
     private User user;
 
+    @OneToMany(mappedBy = "board")
+    private List<Reply> replies = new ArrayList<>();
+
     @CreationTimestamp
     private Timestamp createdAt;
 
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
index d5fc0ee..6ffe66e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
@@ -14,6 +14,13 @@ public interface BoardRepository extends JpaRepository<Board, Integer> {
             countQuery = "select count(b) from Board b where b.title like concat('%', :keyword, '%')")
     Page<Board> findAllJoinUser(@Param("keyword") String keyword, Pageable pageable);
 
-    @Query("select b from Board b join fetch b.user where b.id = :id")
-    Optional<Board> findByIdJoinUser(@Param("id") Integer id);
+    @Query("""
+            select b from Board b
+            join fetch b.user
+            left join fetch b.replies r
+            left join fetch r.user
+            where b.id = :id
+            order by r.id
+            """)
+    Optional<Board> findByIdJoinUserAndReplies(@Param("id") Integer id);
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
index 35f6e25..379f1bc 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
@@ -1,5 +1,6 @@
 package com.newtenco.blog.board;
 
+import com.newtenco.blog.reply.Reply;
 import org.springframework.data.domain.Page;
 
 import java.sql.Timestamp;
@@ -25,10 +26,19 @@ public class BoardResponse {
         }
     }
 
-    public record DetailDTO(Integer id, String title, String content, Integer userId, String username) {
+    public record DetailDTO(Integer id, String title, String content, Integer userId, String username,
+                            List<ReplyDTO> replies) {
 
         public DetailDTO(Board board) {
-            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getId(), board.getUser().getUsername());
+            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getId(), board.getUser().getUsername(),
+                    board.getReplies().stream().map(ReplyDTO::new).toList());
+        }
+
+        public record ReplyDTO(Integer id, String comment, Integer userId, String username) {
+
+            public ReplyDTO(Reply reply) {
+                this(reply.getId(), reply.getComment(), reply.getUser().getId(), reply.getUser().getUsername());
+            }
         }
     }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
index 4c1e3b0..9aa8b51 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
@@ -59,7 +59,7 @@ public class BoardService {
     }
 
     public BoardResponse.DetailDTO 글상세(Integer id) {
-        Board board = boardRepository.findByIdJoinUser(id)
+        Board board = boardRepository.findByIdJoinUserAndReplies(id)
                 .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
         return new BoardResponse.DetailDTO(board);
     }
```

```java
// server-spring/src/main/java/com/newtenco/blog/reply/Reply.java
package com.newtenco.blog.reply;

import com.newtenco.blog.board.Board;
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
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "reply_tb")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Reply(Integer id, String comment, User user, Board board, Timestamp createdAt) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.board = board;
        this.createdAt = createdAt;
    }
}
```

```diff
diff --git a/server-spring/src/main/resources/db/data.sql b/server-spring/src/main/resources/db/data.sql
index 3d201fe..b5ce189 100644
--- a/server-spring/src/main/resources/db/data.sql
+++ b/server-spring/src/main/resources/db/data.sql
@@ -6,3 +6,9 @@ insert into board_tb (title, content, user_id, created_at) values ('제목2', '
 insert into board_tb (title, content, user_id, created_at) values ('제목3', '내용3', 1, now());
 insert into board_tb (title, content, user_id, created_at) values ('제목4', '내용4', 2, now());
 insert into board_tb (title, content, user_id, created_at) values ('제목5', '내용5', 2, now());
+
+insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글1', 4, 2, now());
+insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글2', 4, 2, now());
+insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글3', 4, 2, now());
+insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글4', 5, 1, now());
+insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글5', 5, 1, now());
```

## T2 댓글 쓰기
커밋: 578b50e

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyRepository.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyRequest.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyResponse.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java | 새로 |

```java
// server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java
package com.newtenco.blog.reply;

import com.newtenco.blog.core.util.Resp;
import com.newtenco.blog.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/api/replies")
    public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @Valid @RequestBody ReplyRequest.SaveDTO reqDTO) {
        return Resp.ok(replyService.댓글쓰기(reqDTO, sessionUser));
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/reply/ReplyRepository.java
package com.newtenco.blog.reply;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/reply/ReplyRequest.java
package com.newtenco.blog.reply;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReplyRequest {

    public record SaveDTO(
            @NotBlank(message = "댓글을 입력해주세요")
            @Size(max = 100, message = "댓글은 100자 이하로 입력해주세요")
            String comment,

            @NotNull(message = "게시글 번호가 필요합니다")
            Integer boardId) {

        public Reply toEntity(User user, Board board) {
            return Reply.builder()
                    .comment(comment)
                    .user(user)
                    .board(board)
                    .build();
        }
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/reply/ReplyResponse.java
package com.newtenco.blog.reply;

public class ReplyResponse {

    public record DTO(Integer id, String comment, Integer boardId, Integer userId, String username) {

        public DTO(Reply reply) {
            this(reply.getId(), reply.getComment(), reply.getBoard().getId(), reply.getUser().getId(), reply.getUser().getUsername());
        }
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java
package com.newtenco.blog.reply;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public ReplyResponse.DTO 댓글쓰기(ReplyRequest.SaveDTO reqDTO, User sessionUser) {
        Board board = boardRepository.findById(reqDTO.boardId())
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        Reply savedReply = replyRepository.save(reqDTO.toEntity(sessionUser, board));
        return new ReplyResponse.DTO(savedReply);
    }
}
```

## T3 댓글 삭제
커밋: 2c051e6

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java | 고침 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java b/server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java
index 068dc91..6dfee1e 100644
--- a/server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java
@@ -6,6 +6,8 @@ import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
+import org.springframework.web.bind.annotation.DeleteMapping;
+import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RestController;
@@ -20,4 +22,10 @@ public class ReplyController {
     public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @Valid @RequestBody ReplyRequest.SaveDTO reqDTO) {
         return Resp.ok(replyService.댓글쓰기(reqDTO, sessionUser));
     }
+
+    @DeleteMapping("/api/replies/{id}")
+    public ResponseEntity<?> delete(@AuthenticationPrincipal User sessionUser, @PathVariable("id") Integer id) {
+        replyService.댓글삭제(id, sessionUser.getId());
+        return Resp.ok(null);
+    }
 }
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java b/server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java
index ed7046c..20aa321 100644
--- a/server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java
+++ b/server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.reply;
 
 import com.newtenco.blog.board.Board;
 import com.newtenco.blog.board.BoardRepository;
+import com.newtenco.blog.core.handler.errors.ForbiddenException;
 import com.newtenco.blog.core.handler.errors.NotFoundException;
 import com.newtenco.blog.user.User;
 import lombok.RequiredArgsConstructor;
@@ -23,4 +24,14 @@ public class ReplyService {
         Reply savedReply = replyRepository.save(reqDTO.toEntity(sessionUser, board));
         return new ReplyResponse.DTO(savedReply);
     }
+
+    @Transactional
+    public void 댓글삭제(Integer id, Integer sessionUserId) {
+        Reply reply = replyRepository.findById(id)
+                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다"));
+        if (!reply.getUser().getId().equals(sessionUserId)) {
+            throw new ForbiddenException("댓글을 삭제할 권한이 없습니다");
+        }
+        replyRepository.delete(reply);
+    }
 }
```

## T4 글 지울 때 댓글도 지우기
커밋: b4e0404

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 |

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/Board.java b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
index a33a7c5..68ba2d3 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/Board.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/Board.java
@@ -2,6 +2,7 @@ package com.newtenco.blog.board;
 
 import com.newtenco.blog.reply.Reply;
 import com.newtenco.blog.user.User;
+import jakarta.persistence.CascadeType;
 import jakarta.persistence.Column;
 import jakarta.persistence.Entity;
 import jakarta.persistence.FetchType;
@@ -39,7 +40,7 @@ public class Board {
     @ManyToOne(fetch = FetchType.LAZY)
     private User user;
 
-    @OneToMany(mappedBy = "board")
+    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
     private List<Reply> replies = new ArrayList<>();
 
     @CreationTimestamp
```

