# admin-roles 작업

- [x] 테스트 작성
- [x] T1 회원 역할 바꾸기
  - 근거: S1, S2, S3
  - 파일: server-spring/src/main/java/com/newtenco/blog/user/User.java, server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java, server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java, server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java, server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
  - 실행하면: cos 토큰으로 PUT /api/admin/users/1 roles USER, ADMIN이 200이고 ssar이 다시 로그인하면 관리자 주소가 열린다
  - Why: 역할 목록은 별도 테이블이라 목록 내용을 바꾸면 JPA가 그 테이블 줄을 고친다
  - 다른 장: user-join, admin-api
- [x] T2 역할 검사하기
  - 근거: S4, S5, S6
  - 파일: server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java, server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java, server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java
  - 실행하면: roles에 MANAGER를 넣으면 400 역할은 USER, ADMIN 만 사용할 수 있습니다
  - Why: 없는 역할이나 쓸모없는 수정이 DB에 들어가기 전에 막는다
  - 다른 장: user-join, admin-api
- [x] 수렴 점검
- [x] 캡처
- [x] 도식
