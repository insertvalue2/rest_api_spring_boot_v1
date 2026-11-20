# admin-api 관리자 API

종류: 기능
배울 것: 역할, hasRole, 관리자 전용 주소

## 할 일
- S1 서버를 띄우면 회원 역할이 들어간다: ssar은 USER, cos는 USER와 ADMIN. 새로 가입한 회원은 USER다
- S2 로그인 토큰 안에 역할 목록(roles)이 들어 있다
- S3 GET /api/admin/hello는 ADMIN 토큰이면 "관리자 cos님 환영합니다"를, USER 토큰이면 403 "권한이 없습니다"를 돌려준다
- S4 DELETE /api/admin/boards/{id}는 관리자가 작성자와 상관없이 글을 지운다(댓글도 함께). 없는 글은 404 "게시글을 찾을 수 없습니다"
- S5 관리자 주소가 아닌 /api/** 주소는 지금처럼 USER와 ADMIN 모두 쓸 수 있다

## 요청과 응답
GET /api/admin/hello (cos 토큰)
응답: { "status": 200, "msg": "성공", "body": "관리자 cos님 환영합니다" }

DELETE /api/admin/boards/1 (cos 토큰, ssar의 글)
응답: { "status": 200, "msg": "성공", "body": null }

## 흐름
로그인 -> 토큰에 roles -> 요청 -> JWT 필터가 roles로 권한(ROLE_USER, ROLE_ADMIN)을 만들어 넣음 -> URL 규칙 hasRole("ADMIN") 검사 -> 통과하면 관리자 기능 / 아니면 403

## 안 하는 것
- 관리자가 회원 역할 바꾸기: admin-roles 장
- 관리자 화면: 안 만듦
