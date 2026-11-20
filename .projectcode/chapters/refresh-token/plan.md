# refresh-token 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshToken.java | 새로 | refresh_token_tb 엔티티 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java | 새로 | 토큰 문자열로 찾기 |
| server-spring/src/main/java/com/newtenco/blog/auth/RefreshTokenService.java | 새로 | 발급, 재발급, 폐기 |
| server-spring/src/main/java/com/newtenco/blog/auth/AuthRequest.java | 새로 | RefreshDTO와 입력 검사 |
| server-spring/src/main/java/com/newtenco/blog/auth/AuthResponse.java | 새로 | ReissueDTO(accessToken, refreshToken) |
| server-spring/src/main/java/com/newtenco/blog/auth/AuthController.java | 새로 | POST /reissue, POST /logout |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 고침 | 로그인 결과에 refreshToken |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 | 로그인 때 리프레시 토큰 발급 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 | 로그인 응답 헤더 Refresh-Token |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 고침 | Refresh-Token 노출, /reissue와 /logout 열기, 기본 로그아웃 끄기 |

## 코드 계획
RefreshToken.java
- id, token(unique, 100), @ManyToOne(LAZY) user, expiresAt, @Builder 생성자
- isExpired(): expiresAt이 지금보다 앞이면 true

RefreshTokenRepository.java
- findByTokenJoinUser(token): join fetch rt.user
- findByToken(token)

RefreshTokenService.java
- HEADER "Refresh-Token", EXPIRATION_TIME 14일
- @Transactional 발급(User): UUID 문자열과 만료 시각으로 저장하고 문자열을 돌려준다
- @Transactional 재발급(token): 없으면 UnauthorizedException("다시 로그인해주세요"), 만료면 같은 예외, 쓴 토큰을 지우고 JwtUtil.create와 발급으로 ReissueDTO
- 만료를 먼저 확인한다. 지운 뒤 예외를 던지면 트랜잭션이 되돌려져 지운 것도 취소되기 때문이다
- @Transactional 폐기(token): 있으면 지운다
- 나누는 이유: 리프레시 토큰은 저장, 회전, 폐기 규칙이 있는 별도 기능이라 auth 패키지로 둔다

AuthRequest.java
- record RefreshDTO(@NotBlank("리프레시 토큰이 필요합니다") String refreshToken)

AuthResponse.java
- record ReissueDTO(String accessToken, String refreshToken)

AuthController.java
- POST /reissue: 두 토큰을 헤더 Authorization, Refresh-Token에 담고 body null
- POST /logout: 폐기 후 Resp.ok(null)

UserResponse.java
- LoginDTO에 refreshToken을 더한다

UserService.java
- RefreshTokenService를 받는다. 로그인을 @Transactional로 바꾸고 발급(user) 결과를 LoginDTO에 담는다

UserController.java
- 로그인 응답에 .header(RefreshTokenService.HEADER, loginDTO.refreshToken())

SecurityConfig.java
- setExposedHeaders(List.of("Authorization", "Refresh-Token"))
- http.logout(logout -> logout.disable()): 스프링 기본 /logout 처리를 끈다
- .requestMatchers(HttpMethod.POST, "/reissue", "/logout").permitAll()

## 핵심 코드
if (refreshToken.isExpired()) {
    throw new UnauthorizedException("다시 로그인해주세요");
}
refreshTokenRepository.delete(refreshToken);
String newAccessToken = JwtUtil.create(refreshToken.getUser());
String newRefreshToken = 발급(refreshToken.getUser());

http.logout(logout -> logout.disable());

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | RefreshToken.java, RefreshTokenRepository.java, RefreshTokenService.java, UserResponse.java, UserService.java, UserController.java |
| S2 | RefreshTokenService.java, AuthResponse.java, AuthController.java |
| S3 | RefreshTokenService.java |
| S4 | RefreshTokenService.java, RefreshToken.java |
| S5 | RefreshTokenService.java, AuthController.java, SecurityConfig.java |
| S6 | AuthRequest.java, AuthController.java |
| S7 | SecurityConfig.java |

## 테스트 확인 방법
- S1 로그인 응답에 Refresh-Token 헤더가 있고 refresh_token_tb에 그 값이 있으며 만료가 약 14일 뒤
- S2 재발급이 200, 두 헤더가 있고 Authorization은 Bearer 로 시작, 새 리프레시 토큰은 처음 것과 다르다
- S3 같은 리프레시 토큰으로 두 번째 재발급이 401
- S4 엉터리 값이 401, DB에서 만료 시각을 과거로 바꾼 토큰이 401
- S5 로그아웃이 200이고 그 값으로 재발급이 401
- S6 빈 값이 400
- S7 Origin 5173으로 로그인하면 Access-Control-Expose-Headers에 Refresh-Token
- 테스트마다 refresh_token_tb를 비운다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java: user-join
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java: jwt-auth
