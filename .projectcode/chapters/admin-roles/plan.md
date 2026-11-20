# admin-roles 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 | updateRoles |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 | 역할 수정 요청 RolesDTO |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 | 역할 수정 응답 RolesDTO(id, username, roles) |
| server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java | 고침 | 관리자_역할수정 |
| server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java | 고침 | PUT /api/admin/users/{id} |

## 코드 계획
User.java
- updateRoles(List<String> roles): this.roles.clear(), this.roles.addAll(roles). 목록을 통째로 바꾸지 않고 내용만 바꿔야 JPA가 user_role_tb를 지우고 다시 넣는다

UserRequest.java
- record RolesDTO(@NotEmpty("역할을 설정해주세요") List<String> roles)

UserResponse.java
- record RolesDTO(Integer id, String username, List<String> roles), User를 받는 생성자
- 회원가입, 내 정보 응답 DTO에는 roles를 넣지 않는다

AdminService.java
- UserRepository를 더 받는다
- @Transactional 관리자_역할수정(id, RolesDTO): findById, 없으면 NotFoundException("회원을 찾을 수 없습니다")
- 요청 목록을 LinkedHashSet으로 바꿔 중복을 없앤다
- Set.of("USER", "ADMIN")에 모두 들어 있지 않으면 BadRequestException("역할은 USER, ADMIN 만 사용할 수 있습니다")
- 지금 역할을 HashSet으로 바꿔 같으면 BadRequestException("현재와 같은 역할입니다")
- user.updateRoles(List.copyOf(요청 Set))

AdminController.java
- PUT /api/admin/users/{id}: @Valid @RequestBody UserRequest.RolesDTO

## 핵심 코드
public void updateRoles(List<String> roles) {
    this.roles.clear();
    this.roles.addAll(roles);
}

Set<String> requestedRoles = new LinkedHashSet<>(reqDTO.roles());
if (!ALLOWED_ROLES.containsAll(requestedRoles)) {
    throw new BadRequestException("역할은 USER, ADMIN 만 사용할 수 있습니다");
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | User.java, UserRequest.java, UserResponse.java, AdminService.java, AdminController.java |
| S2 | User.java (admin-api의 토큰 roles) |
| S3 | AdminService.java |
| S4 | UserRequest.java, AdminController.java |
| S5 | AdminService.java |
| S6 | AdminService.java |

## 테스트 확인 방법
- S1 cos 토큰으로 ssar을 USER, ADMIN으로 바꾸면 200, body roles가 USER, ADMIN, user_role_tb에 ssar ADMIN 줄
- S2 바꾼 뒤 ssar이 새로 로그인한 토큰으로 GET /api/admin/hello가 200
- S3 회원 99가 404
- S4 빈 목록이 400
- S5 MANAGER가 들어 있으면 400
- S6 ssar을 USER로 바꾸면 400
- 테스트마다 ssar의 역할을 USER 하나로 되돌린다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/user/User.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java: user-join
- server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java: admin-api
- server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java: admin-api
