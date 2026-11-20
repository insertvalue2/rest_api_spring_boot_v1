# board-update-delete 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/ForbiddenException.java | 새로 | 403 사용자 예외 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 | 403 처리 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 | 수정 정보 조회는 로그인 필요 |
| server-spring/src/main/java/com/newtenco/blog/board/Board.java | 고침 | update(title, content) |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java | 고침 | 수정 요청 UpdateDTO와 입력 검사 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 | 글수정정보, 글수정, 글삭제 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 | GET edit, PUT, DELETE |

## 코드 계획
ForbiddenException.java
- NotFoundException과 같은 모양

GlobalExceptionHandler.java
- ForbiddenException -> 403, 예외 메시지

SecurityConfig.java
- .requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()를 GET /api/boards/** permitAll보다 앞에 둔다

Board.java
- update(String title, String content): 두 값만 바꾼다. save를 부르지 않아도 트랜잭션 끝에 변경 감지로 UPDATE가 나간다

BoardRequest.java
- record UpdateDTO(title, content), SaveDTO와 같은 @NotBlank, @Size 문구

BoardService.java
- 글수정정보(id, sessionUserId): findById, 없으면 NotFoundException("게시글을 찾을 수 없습니다"), 작성자가 아니면 ForbiddenException("게시글에 접근할 권한이 없습니다"), ListDTO
- @Transactional 글수정(id, UpdateDTO, sessionUserId): 같은 검사, 문구는 "게시글을 수정할 권한이 없습니다", board.update, ListDTO
- @Transactional 글삭제(id, sessionUserId): 같은 검사, 문구는 "게시글을 삭제할 권한이 없습니다", boardRepository.delete

BoardController.java
- GET /api/boards/{id}/edit, PUT /api/boards/{id}(@Valid UpdateDTO), DELETE /api/boards/{id}(Resp.ok(null))
- 모두 @AuthenticationPrincipal User sessionUser

## 핵심 코드
if (!board.getUser().getId().equals(sessionUserId)) {
    throw new ForbiddenException("게시글을 수정할 권한이 없습니다");
}
board.update(reqDTO.title(), reqDTO.content());

public void update(String title, String content) {
    this.title = title;
    this.content = content;
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | BoardService.java, BoardController.java |
| S2 | Board.java, BoardRequest.java, BoardService.java, BoardController.java |
| S3 | BoardService.java, BoardController.java |
| S4 | ForbiddenException.java, GlobalExceptionHandler.java, BoardService.java |
| S5 | BoardService.java |
| S6 | BoardRequest.java, BoardController.java |
| S7 | SecurityConfig.java |

## 테스트 확인 방법
- S1 ssar 토큰으로 GET /api/boards/1/edit가 제목1, 내용1
- S2 PUT /api/boards/1이 제목1 수정을 돌려주고 GET /api/boards/1도 바뀌어 있다
- S3 새로 쓴 글을 DELETE하면 200, 그 글 상세가 404
- S4 ssar 토큰으로 글 4(cos)의 edit, PUT, DELETE가 403과 각 문구
- S5 글 99의 edit, PUT, DELETE가 404
- S6 PUT 제목 빈 값이 400 제목을 입력해주세요
- S7 토큰 없이 GET /api/boards/1/edit가 401, GET /api/boards/1은 200
- 글 1은 테스트마다 제목1, 내용1로 되돌리고, 새로 쓴 글은 지운다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java: board-detail
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java: jwt-auth
- server-spring/src/main/java/com/newtenco/blog/board/Board.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java: board-write
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java: board-list
