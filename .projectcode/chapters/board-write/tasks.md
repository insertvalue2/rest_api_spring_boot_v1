# board-write 작업

- [x] 테스트 작성
- [x] T1 글쓰기 API 만들기
  - 근거: S1, S2, S3
  - 파일: server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java, server-spring/src/main/java/com/newtenco/blog/board/Board.java, server-spring/src/main/java/com/newtenco/blog/board/BoardService.java, server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
  - 실행하면: 토큰을 담아 POST /api/boards를 부르면 id 6, username ssar인 글이 오고, 목록 첫 줄이 제목6이다
  - Why: 토큰에서 꺼낸 로그인 사용자를 작성자로 넣어야 누가 쓴 글인지 남는다
  - 다른 장: board-list
- [x] T2 입력 검사하기
  - 근거: S4, S5
  - 파일: server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java, server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
  - 실행하면: 제목을 비우면 400 제목을 입력해주세요
  - Why: 빈 글이나 칸보다 긴 글이 DB까지 가기 전에 막는다
  - 다른 장: board-list
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
