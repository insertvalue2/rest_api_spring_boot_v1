# board-owner 내 글 내 댓글 표시

종류: 기능
배울 것: 선택 인증, owner 계산

## 할 일
- S1 토큰 없이 글 상세를 보면 글의 owner와 모든 댓글의 owner가 false다
- S2 작성자의 토큰으로 자기 글 상세를 보면 글의 owner가 true다
- S3 남의 글에 내가 단 댓글이 있으면, 글의 owner는 false이고 내 댓글만 owner가 true다
- S4 틀린 토큰으로 글 상세를 보면 막히지 않고 로그인하지 않은 것처럼 owner가 모두 false다

## 요청과 응답
GET /api/boards/5 (ssar 토큰)
응답: { "status": 200, "msg": "성공", "body": { "id": 5, "title": "제목5", "content": "내용5", "userId": 2, "username": "cos", "owner": false,
        "replies": [ { "id": 4, "comment": "댓글4", "userId": 1, "username": "ssar", "owner": true }, ... ] } }

## 흐름
GET /api/boards/{id} -> JWT 필터가 토큰이 있으면 로그인 사용자를 넣고, 없거나 틀리면 그냥 지나감 -> 컨트롤러가 로그인 사용자 번호(없으면 null)를 넘김 -> 글 작성자, 댓글 작성자와 비교해 owner 계산 -> 응답

## 안 하는 것
- 목록에 owner 표시: 안 만듦
