# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-역할-바꾸기.json | admin-roles | T1 | 6352eed | PUT /api/admin/users/1 | cos 토큰으로 ssar의 roles를 USER, ADMIN으로 바꾸면 200이고 id 1, ssar, roles USER, ADMIN이 온다 |
| T2-없는-역할.json | admin-roles | T2 | 7900789 | PUT /api/admin/users/1 | roles에 MANAGER를 넣으면 400과 역할은 USER, ADMIN 만 사용할 수 있습니다가 온다 |
| done-역할-바꾸기.json | admin-roles | done | 7900789 | PUT /api/admin/users/1 | cos 토큰으로 ssar의 roles를 USER, ADMIN으로 바꾸면 200이고 id 1, ssar, roles USER, ADMIN이 온다 |
