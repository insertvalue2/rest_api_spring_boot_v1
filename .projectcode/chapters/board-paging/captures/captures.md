# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-3개씩-나누기.json | board-paging | T1 | 5817c74 | GET /api/boards?page=1 | ?page=1이 글 2, 1을 준다. body는 아직 배열이다 |
| T2-쪽-정보.json | board-paging | T2 | 76404c9 | GET /api/boards?page=0 | body가 boards와 number 0, totalPage 2, isFirst true, isLast false로 온다 |
| T3-제목-검색.json | board-paging | T3 | 518355d | GET /api/boards?page=0&keyword=제목1 | keyword 제목1이 글 하나만 주고 totalPage는 1이다 |
| done-쪽-나눈-목록.json | board-paging | done | 518355d | GET /api/boards?page=0 | 첫 쪽 글 5, 4, 3이 number 0, totalPage 2, isFirst true, isLast false와 함께 온다 |
