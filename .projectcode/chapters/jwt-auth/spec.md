# jwt-auth JWT 인증 필터

종류: 기능
배울 것: 인증 필터, Stateless, URL 접근 규칙, 401 JSON, 내 정보 조회

## 할 일
- S1 GET /api/users/me에 로그인 토큰(Authorization: Bearer ...)을 담아 보내면 내 정보(id, username, email)를 돌려준다
- S2 토큰 없이 /api/** 로그인 필요 주소를 부르면 401과 "로그인 후 이용해주세요"를 공통 응답 모양으로 돌려준다
- S3 위조되었거나 만료된 토큰도 401과 같은 문구를 돌려준다
- S4 글 목록, 글 상세, /join, /login, /check-username, H2 콘솔, 브라우저의 사전 요청(OPTIONS)은 토큰 없이 그대로 열린다
- S5 규칙에 없는 주소는 로그인해도 403과 "권한이 없습니다"를 돌려준다
- S6 서버는 로그인 상태를 세션에 저장하지 않는다 (응답에 JSESSIONID 쿠키가 없다)

## 요청과 응답
GET /api/users/me
요청 헤더: Authorization: Bearer eyJ...
응답: { "status": 200, "msg": "성공", "body": { "id": 1, "username": "ssar", "email": "ssar@metacoding.com" } }

GET /api/users/me (토큰 없음)
응답: { "status": 401, "msg": "로그인 후 이용해주세요", "body": null }

GET /nothing (로그인한 토큰)
응답: { "status": 403, "msg": "권한이 없습니다", "body": null }

## 흐름
요청 -> JWT 필터가 헤더에서 토큰 꺼내 검증 -> 맞으면 이번 요청 동안 인증 정보 저장 -> URL 규칙 검사 -> 통과하면 컨트롤러 / 막히면 401 또는 403 JSON

## 안 하는 것
- 토큰 안의 역할(roles)과 관리자 규칙: admin-api 장
- 만료와 위조를 다른 문구로 구분하기: 안 만듦
- 리프레시 토큰: refresh-token 장
