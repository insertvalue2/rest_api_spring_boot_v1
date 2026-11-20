# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-이메일-수정.json | user-update | T1 | 10f05d4 | PUT /api/users/me | ssar 토큰으로 빈 비밀번호와 ssar@nate.com을 보내면 200과 email ssar@nate.com이 온다 |
| T2-비밀번호-수정.json | user-update | T2 | 1c3dbbe | PUT /api/users/me | ssar 토큰으로 비밀번호 5678과 ssar@nate.com을 보내면 200과 email ssar@nate.com이 온다 |
| T3-비밀번호-검사.json | user-update | T3 | 1435cab | PUT /api/users/me | 비밀번호 12를 보내면 400과 비밀번호는 4~20자여야 합니다가 온다 |
| done-내-정보-수정.json | user-update | done | 1435cab | PUT /api/users/me | ssar 토큰으로 빈 비밀번호와 ssar@nate.com을 보내면 200과 id, username, email이 온다 |
