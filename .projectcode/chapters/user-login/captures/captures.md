# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-로그인.json | user-login | T1 | f578c7b | POST /login | ssar/1234로 로그인하면 200과 회원 정보 |
| T2-토큰-헤더.json | user-login | T2 | 6016e4a | POST /login | 응답 헤더 Authorization이 Bearer eyJ로 시작한다 |
| T3-빈-값.json | user-login | T3 | 0483deb | POST /login | 비밀번호를 비우면 400 비밀번호를 입력해주세요 |
| T4-헤더-노출.json | user-login | T4 | 383b2c9 | POST /login | Origin http://localhost:5173으로 로그인하면 Access-Control-Expose-Headers: Authorization이 온다 |
| done-로그인.json | user-login | done | 383b2c9 | POST /login | Origin http://localhost:5173으로 ssar/1234 로그인하면 200, Authorization Bearer 토큰, Access-Control-Expose-Headers: Authorization이 온다 |
