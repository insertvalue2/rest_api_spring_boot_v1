# board-list 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 새로 | 회원 엔티티 user_tb |
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 새로 | 글 엔티티 board_tb, 작성자와 ManyToOne |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 새로 | 글과 작성자를 최신순으로 한 번에 조회 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 새로 | 목록용 응답 DTO |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 새로 | 트랜잭션 안에서 엔티티를 DTO로 바꾼다 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 새로 | GET /api/boards |
| server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java | 새로 | 공통 응답 status, msg, body |
| server-spring/src/main/resources/db/data.sql | 새로 | 회원 2명, 글 5개 |
| server-spring/src/main/resources/application.properties | 고침 | data.sql 위치, 테이블 생성 뒤 실행 |

## 코드 계획
패키지
- 기능별로 나눈다. user, board, core/util
- 나누는 이유: 뒤 장에서 회원, 댓글, 인증이 각자 파일을 늘려 간다

User.java
- @Entity, @Table(name = "user_tb"), @NoArgsConstructor, @Getter
- id(IDENTITY), username(unique, 20), password(60), email(30), createdAt(@CreationTimestamp)
- 역할과 UserDetails는 넣지 않는다

Board.java
- @Entity, @Table(name = "board_tb"), @NoArgsConstructor, @Getter
- id, title(100), content(1000), createdAt
- @ManyToOne(fetch = FetchType.LAZY) User user. FK user_id가 board_tb에 생긴다

BoardRepository.java
- JpaRepository<Board, Integer>를 이어받는다
- join fetch로 작성자까지 한 번에, order by b.id desc로 최신순

BoardResponse.java
- 안에 record ListDTO(id, title, content, username, createdAt)를 둔다
- Board를 받는 생성자에서 board.getUser().getUsername()을 꺼낸다
- 나누는 이유: 엔티티를 그대로 내보내면 password까지 나가고, username이 평평한 칸이 아니다

BoardService.java
- @Service, @RequiredArgsConstructor, @Transactional(readOnly = true)
- 글목록(): 레포지토리 결과를 ListDTO 목록으로 바꾼다
- 나누는 이유: 트랜잭션 경계와 DTO 변환을 컨트롤러 밖에 둔다

BoardController.java
- @RestController, @RequiredArgsConstructor
- GET /api/boards -> Resp.ok(boardService.글목록())

Resp.java
- record Resp<T>(Integer status, String msg, T body)
- static ok(body): 200, "성공"
- 실패용 fail은 board-detail 장에서 더한다

data.sql
- user_tb 2줄(ssar, cos, 비밀번호는 1234의 BCrypt 값), board_tb 5줄(글 1~3은 ssar, 4~5는 cos)

application.properties
- spring.sql.init.data-locations=classpath:db/data.sql
- spring.jpa.defer-datasource-initialization=true

## 핵심 코드
@ManyToOne(fetch = FetchType.LAZY)
private User user;

@Query("select b from Board b join fetch b.user order by b.id desc")
List<Board> findAllJoinUser();

public record ListDTO(Integer id, String title, String content, String username, Timestamp createdAt) {
    public ListDTO(Board board) {
        this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getUsername(), board.getCreatedAt());
    }
}

public record Resp<T>(Integer status, String msg, T body) {
    public static <B> ResponseEntity<Resp<B>> ok(B body) {
        return ResponseEntity.ok(new Resp<>(200, "성공", body));
    }
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | User.java, Board.java, data.sql, application.properties |
| S2 | BoardRepository.java, BoardResponse.java, BoardService.java, BoardController.java |
| S3 | BoardRepository.java |
| S4 | Resp.java, BoardController.java |

## 테스트 확인 방법
- S1 user_tb 2줄, board_tb 5줄
- S2 GET /api/boards의 body에 글 5개, 글마다 username이 있다
- S3 body의 id가 큰 것부터 온다
- S4 응답에 status 200, msg 성공, body가 있다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/resources/application.properties: project-setup
