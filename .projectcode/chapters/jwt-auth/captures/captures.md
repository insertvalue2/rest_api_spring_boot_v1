# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-내-정보.json | jwt-auth | T1 | 7e13dcb | GET /api/users/me | 로그인 토큰을 담아 부르면 200과 ssar 정보가 오고 Set-Cookie가 없다 |
| T2-토큰-없음.json | jwt-auth | T2 | 7b6f27d | GET /api/users/me | 토큰 없이 부르면 401 로그인 후 이용해주세요 |
| T3-규칙-밖-주소.json | jwt-auth | T3 | 31d9d35 | GET /nothing | 토큰을 담아 부르면 403 권한이 없습니다 |
| done-토큰-없음.json | jwt-auth | done | 31d9d35 | GET /api/users/me | 토큰 없이 부르면 401 로그인 후 이용해주세요 |
