# board-detail 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/NotFoundException.java | 새로 | 찾는 것이 없을 때 던지는 사용자 예외 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 새로 | 예외를 공통 실패 응답으로 바꾼다 |
| server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java | 고침 | 실패 응답 fail을 더한다 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 | 글 하나와 작성자를 한 번에 조회 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 | 상세 응답 DetailDTO |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 | 글상세, 없으면 예외 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 | GET /api/boards/{id} |

## 코드 계획
NotFoundException.java
- RuntimeException을 이어받고 메시지만 받는 생성자 하나
- 나누는 이유: 상태 코드마다 예외 이름이 있어야 전역 처리기가 골라 잡는다

GlobalExceptionHandler.java
- @Slf4j, @RestControllerAdvice
- NotFoundException -> Resp.fail(404, 예외 메시지)
- Exception -> 스프링이 상태를 아는 예외(ErrorResponse)는 그 상태와 이유 문구, 나머지는 log.error 후 500 "관리자에게 문의하세요"

Resp.java
- static fail(HttpStatus status, String msg): body는 null

BoardRepository.java
- findByIdJoinUser(id): join fetch b.user where b.id = :id, Optional로 받는다

BoardResponse.java
- record DetailDTO(id, title, content, userId, username), Board를 받는 생성자

BoardService.java
- 글상세(id): 없으면 NotFoundException("게시글을 찾을 수 없습니다")

BoardController.java
- @GetMapping("/api/boards/{id}"), @PathVariable("id") Integer id -> Resp.ok(boardService.글상세(id))

## 핵심 코드
@Query("select b from Board b join fetch b.user where b.id = :id")
Optional<Board> findByIdJoinUser(@Param("id") Integer id);

Board board = boardRepository.findByIdJoinUser(id)
        .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));

@ExceptionHandler(NotFoundException.class)
public ResponseEntity<?> ex404(NotFoundException e) {
    return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
}

@ExceptionHandler(Exception.class)
public ResponseEntity<?> exUnknown(Exception e) {
    if (e instanceof ErrorResponse er) {
        HttpStatus status = HttpStatus.valueOf(er.getStatusCode().value());
        return Resp.fail(status, status.getReasonPhrase());
    }
    log.error("예상하지 못한 오류", e);
    return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의하세요");
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | BoardRepository.java, BoardResponse.java, BoardService.java, BoardController.java |
| S2 | NotFoundException.java, GlobalExceptionHandler.java, Resp.java, BoardService.java |
| S3 | GlobalExceptionHandler.java |
| S4 | GlobalExceptionHandler.java |

## 테스트 확인 방법
- S1 GET /api/boards/4의 body에 id 4, userId 2, username cos, 제목과 내용이 있다
- S2 GET /api/boards/99가 404, msg 게시글을 찾을 수 없습니다, body null
- S3 GET /api/boards/abc가 500, msg 관리자에게 문의하세요, body null
- S4 GET /없는주소가 404이고 status, msg, body 세 칸이 있다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java: board-list
