# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-로그인-토큰.json | admin-api | T1 | 44b26ef | POST /login | cos로 로그인하면 200과 Authorization 헤더에 Bearer 토큰이 온다 |
| T2-관리자-환영.json | admin-api | T2 | ee4d3b2 | GET /api/admin/hello | cos 토큰으로 부르면 200과 관리자 cos님 환영합니다가 온다 |
| T3-관리자-글삭제.json | admin-api | T3 | 1acf4ef | DELETE /api/admin/boards/1 | cos 토큰으로 ssar의 글 1을 지우면 200이 온다 |
| done-관리자-권한.json | admin-api | done | 1acf4ef | GET /api/admin/hello | ssar 토큰으로 부르면 403과 권한이 없습니다가 온다 |
