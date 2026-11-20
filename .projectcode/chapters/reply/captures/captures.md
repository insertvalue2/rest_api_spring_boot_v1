# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-글-상세-댓글.json | reply | T1 | 4ef779e | GET /api/boards/4 | replies에 댓글1~3이 오고 작성자는 cos다 |
| T2-댓글-쓰기.json | reply | T2 | 578b50e | POST /api/replies | ssar 토큰으로 보내면 200이고 id 6인 댓글6이 온다 |
| T3-댓글-삭제-권한.json | reply | T3 | 2c051e6 | DELETE /api/replies/1 | ssar 토큰으로 cos 댓글을 지우면 403 댓글을 삭제할 권한이 없습니다 |
| T4-글-삭제-댓글도.json | reply | T4 | b4e0404 | DELETE /api/boards/4 | cos 토큰으로 댓글이 있는 글 4를 지우면 200이다 |
| done-글-상세.json | reply | done | b4e0404 | GET /api/boards/4 | 글 4 상세에 replies로 댓글 목록이 담겨 온다 |
