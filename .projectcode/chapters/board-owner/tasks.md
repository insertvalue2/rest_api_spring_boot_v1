# board-owner 작업

- [x] 테스트 작성
- [x] T1 내 글 표시하기
  - 근거: S1, S2, S4
  - 파일: server-spring/src/main/java/com/newtenco/blog/board/BoardController.java, server-spring/src/main/java/com/newtenco/blog/board/BoardService.java, server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
  - 실행하면: ssar 토큰으로 GET /api/boards/1이 owner true, 토큰 없이는 false
  - Why: 누구나 보는 주소라도 토큰이 있으면 누구인지 알아내 화면이 수정과 삭제 버튼을 보일지 정하게 한다
  - 다른 장: board-list
- [x] T2 내 댓글 표시하기
  - 근거: S3
  - 파일: server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
  - 실행하면: ssar 토큰으로 GET /api/boards/5가 글 owner false, 댓글4, 5 owner true
  - Why: 댓글마다 작성자가 다르므로 댓글 하나하나에 owner를 계산한다
  - 다른 장: board-list
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
