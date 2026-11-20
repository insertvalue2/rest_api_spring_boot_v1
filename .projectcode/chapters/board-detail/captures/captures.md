# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-글-상세.json | board-detail | T1 | 8b2a572 | GET /api/boards/4 | 글 4가 작성자 번호 2, 아이디 cos와 함께 온다 |
| T2-없는-글-404.json | board-detail | T2 | 6c6d77d | GET /api/boards/99 | status 404, msg 게시글을 찾을 수 없습니다, body null로 온다 |
| T3-처리못한-오류-500.json | board-detail | T3 | d806b29 | GET /api/boards/abc | status 500, msg 관리자에게 문의하세요로 온다 |
| T4-없는-주소-404.json | board-detail | T4 | ac823a9 | GET /api/nothing-here | 없는 주소가 status 404로 온다 |
| done-없는-글-404.json | board-detail | done | ac823a9 | GET /api/boards/99 | status 404, msg 게시글을 찾을 수 없습니다, body null인 공통 실패 응답이 온다 |
