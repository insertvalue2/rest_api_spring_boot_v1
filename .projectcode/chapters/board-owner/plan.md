# board-owner 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 | 상세에서 로그인 사용자를 받되 없으면 null |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 | 글상세가 로그인 사용자 번호를 받는다 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 | 상세와 댓글에 owner |

## 코드 계획
BoardController.java
- detail(@AuthenticationPrincipal User sessionUser, id)
- 토큰이 없거나 틀리면 sessionUser는 null이다. sessionUserId = sessionUser == null ? null : sessionUser.getId()

BoardService.java
- 글상세(id, sessionUserId): DetailDTO(board, sessionUserId)

BoardResponse.java
- DetailDTO에 boolean owner: board.getUser().getId().equals(sessionUserId)
- ReplyDTO에 boolean owner: reply.getUser().getId().equals(sessionUserId)
- sessionUserId가 null이면 equals가 false라 따로 나누지 않는다

## 핵심 코드
Integer sessionUserId = sessionUser == null ? null : sessionUser.getId();

board.getUser().getId().equals(sessionUserId)

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | BoardController.java, BoardResponse.java |
| S2 | BoardController.java, BoardService.java, BoardResponse.java |
| S3 | BoardResponse.java |
| S4 | BoardController.java (jwt-auth 필터가 틀린 토큰을 그냥 지나가게 한다) |

## 테스트 확인 방법
- S1 토큰 없이 글 4 상세의 owner와 댓글 owner가 모두 false
- S2 ssar 토큰으로 글 1 상세의 owner가 true
- S3 ssar 토큰으로 글 5 상세의 owner가 false, 댓글 4, 5의 owner가 true
- S4 Bearer abc.def.ghi로 글 1 상세가 200이고 owner false

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java: board-list
