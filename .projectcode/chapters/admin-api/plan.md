# admin-api 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/admin/AdminController.java | 새로 | GET /api/admin/hello, DELETE /api/admin/boards/{id} |
| server-spring/src/main/java/com/newtenco/blog/admin/AdminService.java | 새로 | 관리자_게시글삭제 |
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 | 역할 목록 roles, 권한 목록 getAuthorities |
| server-spring/src/main/resources/db/data.sql | 고침 | user_role_tb 3줄 |
| server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java | 고침 | 토큰에 roles 넣고 되살리기 |
| server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java | 고침 | 인증 정보에 권한 목록을 넣는다 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 | /api/admin/** hasRole ADMIN, /api/** hasAnyRole |

## 코드 계획
User.java
- @ElementCollection(fetch = FetchType.EAGER), @CollectionTable(name = "user_role_tb", joinColumns = @JoinColumn(name = "user_id")), @Column(name = "role") List<String> roles = new ArrayList<>(List.of("USER"))
- 빌더 생성자에 roles를 더한다. null이면 기본값 USER를 그대로 둔다
- getAuthorities(): roles마다 "ROLE_"을 붙인 SimpleGrantedAuthority 목록
- EAGER 이유: 로그인과 토큰 만들기에서 늘 역할이 필요하다

data.sql
- user_role_tb: (1, USER), (2, USER), (2, ADMIN)

JwtUtil.java
- create: withClaim("roles", user.getRoles())
- verify: getClaim("roles").asList(String.class)로 User.builder().roles(...)

JwtAuthorizationFilter.java
- new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())

SecurityConfig.java
- .requestMatchers("/api/admin/**").hasRole("ADMIN")를 GET /api/boards/** permitAll보다 앞에 둔다
- .requestMatchers("/api/**").authenticated()를 .hasAnyRole("USER", "ADMIN")으로 바꾼다

AdminService.java
- @Transactional 관리자_게시글삭제(id): findById, 없으면 NotFoundException("게시글을 찾을 수 없습니다"), delete. 작성자는 확인하지 않는다
- 나누는 이유: 관리자 기능은 작성자 검사가 없는 다른 규칙이라 admin 패키지에 따로 둔다

AdminController.java
- GET /api/admin/hello: Resp.ok("관리자 " + sessionUser.getUsername() + "님 환영합니다")
- DELETE /api/admin/boards/{id}: Resp.ok(null)

## 핵심 코드
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "user_role_tb", joinColumns = @JoinColumn(name = "user_id"))
@Column(name = "role")
private List<String> roles = new ArrayList<>(List.of("USER"));

public List<SimpleGrantedAuthority> getAuthorities() {
    return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
}

.requestMatchers("/api/admin/**").hasRole("ADMIN")

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | User.java, data.sql |
| S2 | JwtUtil.java |
| S3 | User.java, JwtAuthorizationFilter.java, SecurityConfig.java, AdminController.java |
| S4 | AdminService.java, AdminController.java |
| S5 | SecurityConfig.java |

## 테스트 확인 방법
- S1 user_role_tb에 ssar USER, cos USER와 ADMIN. 새로 가입한 회원은 USER 한 줄
- S2 cos 토큰 가운데 조각의 roles가 USER, ADMIN
- S3 cos 토큰으로 hello가 200과 문구, ssar 토큰은 403 권한이 없습니다
- S4 cos 토큰으로 ssar의 새 글을 지우면 200, 그 글 상세 404. 글 99는 404
- S5 ssar 토큰으로 GET /api/users/me가 200
- 앞 장 UserJoinTest, JwtAuthTest는 회원을 지우기 전에 user_role_tb 줄을 먼저 지운다 (Touches: user-join, jwt-auth)

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/user/User.java: user-join
- server-spring/src/main/resources/db/data.sql: board-list
- server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java: user-login
- server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java: jwt-auth
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java: jwt-auth
