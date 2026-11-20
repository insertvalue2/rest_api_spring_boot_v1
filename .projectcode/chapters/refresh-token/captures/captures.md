# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-리프레시-토큰-받기.json | refresh-token | T1 | a200bf8 | POST /login | ssar로 로그인하면 200이고 응답 헤더에 Authorization과 Refresh-Token이 온다 |
| T2-재발급.json | refresh-token | T2 | d1d0643 | POST /reissue | 리프레시 토큰을 보내면 200이고 응답 헤더에 새 Authorization과 새 Refresh-Token이 온다 |
| T3-로그아웃.json | refresh-token | T3 | 2343ab0 | POST /logout | 리프레시 토큰을 보내면 200이고 body는 null이다 |
| done-재발급.json | refresh-token | done | 2343ab0 | POST /reissue | 리프레시 토큰을 보내면 200이고 응답 헤더에 새 Authorization과 새 Refresh-Token이 온다 |
