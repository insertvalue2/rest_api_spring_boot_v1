# board-list 코드 요약

## T1 회원과 글 엔티티 만들기
커밋: cc19286

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 새로 |

```java
// server-spring/src/main/java/com/newtenco/blog/user/User.java
package com.newtenco.blog.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_tb")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 20, nullable = false)
    private String username;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    private String email;

    @CreationTimestamp
    private Timestamp createdAt;
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/board/Board.java
package com.newtenco.blog.board;

import com.newtenco.blog.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "board_tb")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;
}
```

## T2 샘플 데이터 넣기
커밋: 3de650d

| 파일 | 변경 |
|---|---|
| server-spring/src/main/resources/db/data.sql | 새로 |
| server-spring/src/main/resources/application.properties | 고침 |

```sql
// server-spring/src/main/resources/db/data.sql
insert into user_tb (username, password, email, created_at) values ('ssar', '$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO', 'ssar@metacoding.com', now());
insert into user_tb (username, password, email, created_at) values ('cos', '$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO', 'cos@metacoding.com', now());

insert into board_tb (title, content, user_id, created_at) values ('제목1', '내용1', 1, now());
insert into board_tb (title, content, user_id, created_at) values ('제목2', '내용2', 1, now());
insert into board_tb (title, content, user_id, created_at) values ('제목3', '내용3', 1, now());
insert into board_tb (title, content, user_id, created_at) values ('제목4', '내용4', 2, now());
insert into board_tb (title, content, user_id, created_at) values ('제목5', '내용5', 2, now());
```

```diff
diff --git a/server-spring/src/main/resources/application.properties b/server-spring/src/main/resources/application.properties
index 1b4a29a..06e2396 100644
--- a/server-spring/src/main/resources/application.properties
+++ b/server-spring/src/main/resources/application.properties
@@ -12,3 +12,6 @@ spring.jpa.hibernate.ddl-auto=create
 spring.jpa.show-sql=true
 spring.jpa.properties.hibernate.format_sql=true
 spring.jpa.open-in-view=false
+
+spring.sql.init.data-locations=classpath:db/data.sql
+spring.jpa.defer-datasource-initialization=true
```

## T3 글 목록 API 만들기
커밋: 8c73249

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 새로 |

```java
// server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
package com.newtenco.blog.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    @Query("select b from Board b join fetch b.user order by b.id desc")
    List<Board> findAllJoinUser();
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
package com.newtenco.blog.board;

import java.sql.Timestamp;

public class BoardResponse {

    public record ListDTO(Integer id, String title, String content, String username, Timestamp createdAt) {

        public ListDTO(Board board) {
            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getUsername(), board.getCreatedAt());
        }
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
package com.newtenco.blog.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse.ListDTO> 글목록() {
        return boardRepository.findAllJoinUser().stream()
                .map(BoardResponse.ListDTO::new)
                .toList();
    }
}
```

```java
// server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
package com.newtenco.blog.board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/boards")
    public List<BoardResponse.ListDTO> list() {
        return boardService.글목록();
    }
}
```

## T4 공통 응답으로 감싸기
커밋: 2ef17ba

| 파일 | 변경 |
|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java | 새로 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 |

```java
// server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java
package com.newtenco.blog.core.util;

import org.springframework.http.ResponseEntity;

public record Resp<T>(Integer status, String msg, T body) {

    public static <B> ResponseEntity<Resp<B>> ok(B body) {
        return ResponseEntity.ok(new Resp<>(200, "성공", body));
    }
}
```

```diff
diff --git a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
index 8332e28..de350d2 100644
--- a/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
+++ b/server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
@@ -1,11 +1,11 @@
 package com.newtenco.blog.board;
 
+import com.newtenco.blog.core.util.Resp;
 import lombok.RequiredArgsConstructor;
+import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RestController;
 
-import java.util.List;
-
 @RequiredArgsConstructor
 @RestController
 public class BoardController {
@@ -13,7 +13,7 @@ public class BoardController {
     private final BoardService boardService;
 
     @GetMapping("/api/boards")
-    public List<BoardResponse.ListDTO> list() {
-        return boardService.글목록();
+    public ResponseEntity<?> list() {
+        return Resp.ok(boardService.글목록());
     }
 }
```
