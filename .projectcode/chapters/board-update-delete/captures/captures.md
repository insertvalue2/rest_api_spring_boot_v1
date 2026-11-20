# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-수정-정보.json | board-update-delete | T1 | b2f0f4d | GET /api/boards/1/edit | ssar 토큰으로 부르면 200과 제목1, 내용1이 온다 |
| T2-남의-글-수정-정보.json | board-update-delete | T2 | 121259b | GET /api/boards/4/edit | ssar 토큰으로 글 4를 부르면 403 게시글에 접근할 권한이 없습니다 |
| T3-글-수정.json | board-update-delete | T3 | 29db1ad | PUT /api/boards/1 | ssar 토큰으로 고치면 200과 제목1 수정이 온다 |
| T4-글-삭제.json | board-update-delete | T4 | 77c0666 | DELETE /api/boards/6 | 새로 쓴 글 6을 지우면 200과 body null이 온다 |
| done-남의-글-삭제.json | board-update-delete | done | 77c0666 | DELETE /api/boards/4 | ssar 토큰으로 글 4를 지우면 403 게시글을 삭제할 권한이 없습니다 |
