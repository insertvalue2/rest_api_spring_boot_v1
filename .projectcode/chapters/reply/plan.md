# reply 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/reply/Reply.java | 새로 | 댓글 엔티티 reply_tb |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyRepository.java | 새로 | 댓글 저장, 찾기, 지우기 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyRequest.java | 새로 | 댓글 쓰기 요청 SaveDTO와 입력 검사 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyResponse.java | 새로 | 댓글 응답 DTO |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyService.java | 새로 | 댓글쓰기, 댓글삭제 |
| server-spring/src/main/java/com/newtenco/blog/reply/ReplyController.java | 새로 | POST /api/replies, DELETE /api/replies/{id} |
| server-spring/src/main/resources/db/data.sql | 고침 | 댓글 5줄 |
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 | 댓글 목록 OneToMany, 글과 함께 지우기 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 | 상세에서 댓글과 댓글 작성자까지 한 번에 조회 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 | 상세 DTO에 댓글 목록 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 | 글상세가 새 쿼리를 쓴다 |

## 코드 계획
Reply.java
- @Entity reply_tb, id, comment(100), @ManyToOne(LAZY) user, @ManyToOne(LAZY) board, createdAt, @Builder 생성자

ReplyRepository.java
- JpaRepository<Reply, Integer>

ReplyRequest.java
- record SaveDTO(comment, boardId): comment @NotBlank("댓글을 입력해주세요"), @Size(max = 100, "댓글은 100자 이하로 입력해주세요"), boardId @NotNull("게시글 번호가 필요합니다")
- toEntity(User user, Board board)

ReplyResponse.java
- record DTO(id, comment, boardId, userId, username), Reply를 받는 생성자

ReplyService.java
- @Transactional(readOnly = true) 클래스
- @Transactional 댓글쓰기(SaveDTO, User sessionUser): boardRepository.findById, 없으면 NotFoundException("게시글을 찾을 수 없습니다"), save, DTO
- @Transactional 댓글삭제(id, sessionUserId): 없으면 NotFoundException("댓글을 찾을 수 없습니다"), 작성자가 아니면 ForbiddenException("댓글을 삭제할 권한이 없습니다"), delete
- 나누는 이유: 댓글은 글과 다른 주소와 규칙을 가진 기능이라 reply 패키지로 둔다

ReplyController.java
- POST /api/replies(@AuthenticationPrincipal, @Valid SaveDTO), DELETE /api/replies/{id}

data.sql
- reply_tb 5줄: 글 4에 cos의 댓글1~3, 글 5에 ssar의 댓글4~5

Board.java
- @OneToMany(mappedBy = "board") List<Reply> replies = new ArrayList<>(): 글 쪽은 읽기만 한다. 외래키는 reply_tb에 있다
- T4에서 cascade = CascadeType.REMOVE를 더한다

BoardRepository.java
- findByIdJoinUserAndReplies(id): join fetch b.user left join fetch b.replies r left join fetch r.user where b.id = :id order by r.id
- 댓글이 없는 글도 나오게 left join을 쓴다

BoardResponse.java
- DetailDTO에 List<ReplyDTO> replies, 안에 record ReplyDTO(id, comment, userId, username)

BoardService.java
- 글상세가 findByIdJoinUserAndReplies를 쓴다

## 핵심 코드
@OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
private List<Reply> replies = new ArrayList<>();

@Query("""
        select b from Board b
        join fetch b.user
        left join fetch b.replies r
        left join fetch r.user
        where b.id = :id
        order by r.id
        """)
Optional<Board> findByIdJoinUserAndReplies(@Param("id") Integer id);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Reply.java, data.sql |
| S2 | Board.java, BoardRepository.java, BoardResponse.java, BoardService.java |
| S3 | ReplyRepository.java, ReplyRequest.java, ReplyResponse.java, ReplyService.java, ReplyController.java |
| S4 | ReplyService.java |
| S5 | ReplyRequest.java, ReplyController.java |
| S6 | ReplyService.java, ReplyController.java |
| S7 | Board.java |

## 테스트 확인 방법
- S1 reply_tb 5줄
- S2 글 4 상세의 replies가 id 1, 2, 3, 모두 cos. 글 1은 빈 목록
- S3 ssar 토큰으로 글 4에 댓글을 쓰면 200, body에 comment, boardId 4, userId 1, username ssar. 글 4 상세에 그 댓글이 있다
- S4 글 99에 댓글이 404
- S5 빈 댓글, 101자 댓글, 글 번호 없음이 400과 각 문구
- S6 ssar이 자기 댓글을 지우면 200, cos의 댓글 1은 403, 댓글 99는 404
- S7 새 글에 댓글을 단 뒤 글을 지우면 200, 그 댓글이 reply_tb에 없다
- 테스트가 만든 글과 댓글은 지우고, 지운 샘플 댓글은 되돌린다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/resources/db/data.sql: board-list
- server-spring/src/main/java/com/newtenco/blog/board/Board.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java: board-list
