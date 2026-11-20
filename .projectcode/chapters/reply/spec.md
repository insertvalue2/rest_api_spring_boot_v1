# reply 댓글

종류: 기능
배울 것: Reply 엔티티, 댓글 쓰기와 삭제, 상세에 댓글 함께 주기

## 할 일
- S1 서버를 띄우면 댓글 5개가 DB에 들어간다 (글 4에 cos 댓글 3개, 글 5에 ssar 댓글 2개)
- S2 GET /api/boards/{id} 글 상세가 댓글 목록(id, comment, userId, username)을 댓글 번호 순으로 함께 준다. 댓글이 없는 글은 빈 목록이다
- S3 POST /api/replies가 로그인 토큰과 글 번호, 댓글을 받아 저장하고 id, comment, boardId, userId, username을 돌려준다
- S4 없는 글에 댓글을 쓰면 404와 "게시글을 찾을 수 없습니다"를 돌려준다
- S5 댓글이 비었거나 100자를 넘거나 글 번호가 없으면 400과 "댓글을 입력해주세요", "댓글은 100자 이하로 입력해주세요", "게시글 번호가 필요합니다"를 돌려준다
- S6 DELETE /api/replies/{id}가 작성자의 댓글을 지운다. 남의 댓글은 403 "댓글을 삭제할 권한이 없습니다", 없는 댓글은 404 "댓글을 찾을 수 없습니다"
- S7 글을 지우면 그 글의 댓글도 함께 지워진다

## 요청과 응답
GET /api/boards/4
응답: { "status": 200, "msg": "성공", "body": { "id": 4, "title": "제목4", "content": "내용4", "userId": 2, "username": "cos",
        "replies": [ { "id": 1, "comment": "댓글1", "userId": 2, "username": "cos" }, ... ] } }

POST /api/replies (ssar 토큰)
요청: { "boardId": 4, "comment": "댓글6" }
응답: { "status": 200, "msg": "성공", "body": { "id": 6, "comment": "댓글6", "boardId": 4, "userId": 1, "username": "ssar" } }

## 흐름
상세: GET /api/boards/{id} -> 글, 작성자, 댓글, 댓글 작성자를 한 번에 조회 -> 상세와 댓글 목록 응답
쓰기: POST /api/replies -> 입력 검사 -> 글 찾기 -> 로그인 사용자를 작성자로 댓글 저장 -> 응답
삭제: DELETE /api/replies/{id} -> 댓글 찾기 -> 작성자 확인 -> 삭제

## 안 하는 것
- 내 글, 내 댓글 표시(owner): board-owner 장
- 댓글 수정: 안 만듦
