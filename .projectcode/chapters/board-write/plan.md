# board-write 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java | 새로 | 글쓰기 요청 SaveDTO와 입력 검사 |
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 | 저장용 @Builder 생성자 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 | 글쓰기, 쓰기 트랜잭션 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 | POST /api/boards, 로그인 사용자 받기 |

## 코드 계획
BoardRequest.java
- record SaveDTO(title, content)
- title: @NotBlank("제목을 입력해주세요"), @Size(max = 100, "제목은 100자 이하로 입력해주세요")
- content: @NotBlank("내용을 입력해주세요"), @Size(max = 1000, "내용은 1000자 이하로 입력해주세요")
- toEntity(User user): Board.builder()

Board.java
- @Builder 생성자(id, title, content, user, createdAt)

BoardService.java
- @Transactional 글쓰기(SaveDTO reqDTO, User sessionUser): save 후 ListDTO
- 클래스의 readOnly를 이 메서드만 쓰기 트랜잭션으로 덮는다

BoardController.java
- @PostMapping("/api/boards"): @AuthenticationPrincipal User sessionUser, @Valid @RequestBody SaveDTO

## 핵심 코드
@Transactional
public BoardResponse.ListDTO 글쓰기(BoardRequest.SaveDTO reqDTO, User sessionUser) {
    Board savedBoard = boardRepository.save(reqDTO.toEntity(sessionUser));
    return new BoardResponse.ListDTO(savedBoard);
}

@PostMapping("/api/boards")
public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @Valid @RequestBody BoardRequest.SaveDTO reqDTO) {
    return Resp.ok(boardService.글쓰기(reqDTO, sessionUser));
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | BoardRequest.java, Board.java, BoardService.java, BoardController.java |
| S2 | BoardService.java |
| S3 | BoardController.java (jwt-auth의 /api/** 규칙) |
| S4 | BoardRequest.java, BoardController.java |
| S5 | BoardRequest.java |

## 테스트 확인 방법
- S1 ssar 토큰으로 글쓰기가 200, body에 id, 제목, 내용, username ssar
- S2 글쓰기 뒤 GET /api/boards 첫 줄이 새 글
- S3 토큰 없이 글쓰기가 401 로그인 후 이용해주세요
- S4 제목 빈 값, 내용 빈 값이 400과 문구
- S5 제목 101자, 내용 1001자가 400과 문구
- 쓴 글은 테스트마다 지운다. 앞 장의 글 5개 확인을 지키기 위해서다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/board/Board.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java: board-list
