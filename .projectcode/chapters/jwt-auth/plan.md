# jwt-auth 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java | 새로 | 요청마다 토큰을 검사해 인증 정보를 넣는다 |
| server-spring/src/main/java/com/newtenco/blog/core/util/RespFilter.java | 새로 | 필터 단계에서 공통 실패 응답 JSON을 직접 쓴다 |
| server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java | 고침 | 토큰 검증 verify |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 | 세션 없음, 필터 등록, 401과 403 JSON, URL 규칙 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 | 내정보 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 | GET /api/users/me |

## 코드 계획
JwtAuthorizationFilter.java
- OncePerRequestFilter를 이어받는다
- Authorization 헤더가 Bearer 로 시작하면 떼어 내고 JwtUtil.verify
- 맞으면 UsernamePasswordAuthenticationToken(user, null, List.of())을 SecurityContextHolder에 넣는다
- 검증 예외는 잡아서 아무것도 넣지 않는다. 막을지는 URL 규칙이 정한다
- 참고 저장소의 JwtProvider는 두지 않는다. 하는 일이 두 줄이라 필터 안에 쓴다

RespFilter.java
- static fail(HttpServletResponse, int status, String msg): 상태, application/json;charset=utf-8, JsonMapper로 Resp를 글자로 써 넣는다
- 나누는 이유: 필터 단계에서는 전역 예외 처리기가 동작하지 않는다

JwtUtil.java
- static verify(String jwt): JWT.require(ALGORITHM).build().verify(jwt), 클레임 id와 subject로 User.builder()

SecurityConfig.java
- sessionManagement STATELESS
- addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
- exceptionHandling: authenticationEntryPoint -> RespFilter.fail(401, "로그인 후 이용해주세요"), accessDeniedHandler -> RespFilter.fail(403, "권한이 없습니다")
- URL 규칙 (위에서부터)
  - OPTIONS /** permitAll
  - /join, /login, /check-username permitAll
  - /h2-console/** permitAll
  - GET /api/boards/** permitAll
  - /api/** authenticated
  - anyRequest denyAll

UserService.java
- 내정보(Integer id): findById, 없으면 NotFoundException("회원을 찾을 수 없습니다"), UserResponse.DTO

UserController.java
- GET /api/users/me: @AuthenticationPrincipal User sessionUser -> Resp.ok(userService.내정보(sessionUser.getId()))

## 핵심 코드
String header = request.getHeader(JwtUtil.HEADER);
if (header != null && header.startsWith(JwtUtil.TOKEN_PREFIX)) {
    try {
        User user = JwtUtil.verify(header.substring(JwtUtil.TOKEN_PREFIX.length()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (JWTVerificationException e) {
    }
}
filterChain.doFilter(request, response);

http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

.requestMatchers("/api/**").authenticated()
.anyRequest().denyAll()

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | JwtAuthorizationFilter.java, JwtUtil.java, SecurityConfig.java, UserService.java, UserController.java |
| S2 | SecurityConfig.java, RespFilter.java |
| S3 | JwtAuthorizationFilter.java, SecurityConfig.java |
| S4 | SecurityConfig.java |
| S5 | SecurityConfig.java, RespFilter.java |
| S6 | SecurityConfig.java |

## 테스트 확인 방법
- S1 로그인 토큰으로 GET /api/users/me가 200, body에 id 1, ssar, email
- S2 토큰 없이 /api/users/me가 401 로그인 후 이용해주세요, body null
- S3 서명을 바꾼 토큰과 엉터리 토큰이 401 같은 문구. 만료 토큰은 비밀키가 학생 코드 안에 있어 테스트에서 만들 수 없다. 만료도 위조와 같은 JWTVerificationException으로 잡히므로 위조 토큰으로 대신한다
- S4 토큰 없이 글 목록, 글 상세, /join, /check-username, /h2-console이 200, Origin을 담은 OPTIONS 사전 요청이 200
- S5 토큰을 담아 GET /nothing이 403 권한이 없습니다
- S6 토큰 없이 401이 난 응답과 로그인한 응답 모두 Set-Cookie에 JSESSIONID가 없다
- 앞 장 BoardDetailTest S4는 토큰을 담아 보낸다 (Touches: board-detail)

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java: user-login
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java: user-login
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java: user-login
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java: user-login
