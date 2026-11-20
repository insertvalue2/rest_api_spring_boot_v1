# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-목록-API-없음.json | board-list | T1 | cc19286 | GET /api/boards | 엔티티만 있고 목록 API는 아직 없어 status가 404다 |
| T2-목록-API-없음.json | board-list | T2 | 3de650d | GET /api/boards | 샘플 데이터를 넣은 뒤에도 목록 API는 아직 없어 status가 404다 |
| T3-글-목록.json | board-list | T3 | 8c73249 | GET /api/boards | 글 5개가 id 5부터 작성자 아이디와 함께 JSON 배열로 온다 |
| T4-공통-응답.json | board-list | T4 | 2ef17ba | GET /api/boards | 같은 목록이 status 200, msg 성공, body 배열 안에 담겨 온다 |
| done-글-목록.json | board-list | done | 2ef17ba | GET /api/boards | status 200, msg 성공, body에 글 5개가 id 5부터 작성자 아이디와 함께 온다 |
