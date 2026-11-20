# user-login 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/build.gradle | 고침 | java-jwt 4.5.0 |
| server-spring/src/main/resources/application.properties | 고침 | 허용할 출처 목록 |
| server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java | 새로 | JWT 만들기 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/UnauthorizedException.java | 새로 | 401 사용자 예외 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 | 401 처리 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 | CORS 허용과 Authorization 헤더 노출 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java | 고침 | 아이디로 회원 찾기 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 | 로그인 요청 LoginDTO |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 | 로그인 결과 LoginDTO(토큰, 회원) |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 | 로그인 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 | POST /login, 토큰을 헤더에 |

## 코드 계획
JwtUtil.java
- HEADER "Authorization", TOKEN_PREFIX "Bearer ", EXPIRATION_TIME 1일, SECRET 64자 이상, Algorithm.HMAC512
- static create(User): subject username, expiresAt, 클레임 id와 username, 앞에 Bearer 를 붙여 돌려준다
- 검증 verify는 jwt-auth 장에서 더한다
- 나누는 이유: 토큰 만들기는 HTTP와 상관없는 도구라 core/util에 둔다

UnauthorizedException.java
- NotFoundException과 같은 모양

GlobalExceptionHandler.java
- UnauthorizedException -> 401, 예외 메시지

UserRepository.java
- Optional<User> findByUsername(String username)

UserRequest.java
- record LoginDTO(username, password), 각각 @NotBlank 문구

UserResponse.java
- record LoginDTO(String accessToken, DTO user)

UserService.java
- @Transactional(readOnly) 그대로. 로그인(LoginDTO): findByUsername 없으면, matches 틀리면 같은 UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다")
- JwtUtil.create(user)와 DTO를 LoginDTO로 돌려준다

UserController.java
- POST /login: @Valid @RequestBody, ResponseEntity.ok().header(JwtUtil.HEADER, 토큰).body(new Resp<>(200, "성공", 회원))

SecurityConfig.java
- @Value("${app.cors.allowed-origins}") String allowedOrigins
- @Bean CorsConfigurationSource: setAllowedOriginPatterns(콤마로 나눈 목록), 메서드 GET POST PUT DELETE OPTIONS, 헤더 *, setExposedHeaders(List.of("Authorization"))
- http.cors(cors -> cors.configurationSource(corsConfigurationSource()))

application.properties
- app.cors.allowed-origins=http://localhost:3000,http://localhost:5173,chrome-extension://*

## 핵심 코드
String jwt = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .withClaim("id", user.getId())
        .withClaim("username", user.getUsername())
        .sign(ALGORITHM);
return TOKEN_PREFIX + jwt;

if (!passwordEncoder.matches(reqDTO.password(), user.getPassword())) {
    throw new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다");
}

return ResponseEntity.ok()
        .header(JwtUtil.HEADER, loginDTO.accessToken())
        .body(new Resp<>(200, "성공", loginDTO.user()));

config.setExposedHeaders(List.of("Authorization"));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | UserRepository.java, UserRequest.java, UserService.java, UserController.java |
| S2 | JwtUtil.java, UserResponse.java, UserService.java, UserController.java, build.gradle |
| S3 | JwtUtil.java |
| S4 | UnauthorizedException.java, GlobalExceptionHandler.java, UserService.java |
| S5 | UserRequest.java, UserController.java |
| S6 | SecurityConfig.java, application.properties |

## 테스트 확인 방법
- S1 ssar/1234 로그인이 200, body에 id 1, username ssar, email
- S2 응답 헤더 Authorization이 Bearer 로 시작한다
- S3 토큰 가운데 조각을 base64로 풀면 id 1, username ssar, exp가 지금부터 약 1일 뒤
- S4 비밀번호 틀림과 없는 아이디가 모두 401과 같은 문구
- S5 비밀번호 빈 값이 400 비밀번호를 입력해주세요
- S6 Origin http://localhost:5173 요청에 Access-Control-Allow-Origin과 Access-Control-Expose-Headers Authorization

## 라이브러리
- com.auth0:java-jwt:4.5.0 (contract.md)

## 다른 장 파일
- server-spring/build.gradle: project-setup
- server-spring/src/main/resources/application.properties: project-setup
- server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java: board-detail
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java: user-join
