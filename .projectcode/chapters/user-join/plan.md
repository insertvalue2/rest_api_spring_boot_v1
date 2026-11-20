# user-join 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/build.gradle | 고침 | security, validation 스타터 |
| server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java | 새로 | 비밀번호 암호화 도구, 기본 보안 기능 끄기, 모든 주소 열기 |
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 | 저장용 @Builder 생성자 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java | 새로 | 저장, 아이디 있는지 확인 |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 새로 | 가입 요청 JoinDTO와 입력 검사 |
| server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java | 새로 | 회원 응답 DTO, 중복 확인 AvailableDTO |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 새로 | 회원가입, 유저네임중복체크 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 새로 | POST /join, GET /check-username |
| server-spring/src/main/java/com/newtenco/blog/core/handler/errors/BadRequestException.java | 새로 | 400 사용자 예외 |
| server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java | 고침 | 400 처리 세 가지 |

## 코드 계획
build.gradle
- spring-boot-starter-security, spring-boot-starter-validation

SecurityConfig.java
- @Configuration
- @Bean PasswordEncoder: new BCryptPasswordEncoder()
- @Bean SecurityFilterChain
  - headers frameOptions sameOrigin: H2 콘솔 iframe
  - csrf, formLogin, httpBasic 끄기
  - authorizeHttpRequests anyRequest permitAll. 주소별 규칙은 jwt-auth 장
- 나누는 이유: 보안 설정은 기능 패키지가 아니라 앱 전체 설정이라 core에 둔다

User.java
- @Builder를 붙인 생성자(id, username, password, email, createdAt)

UserRepository.java
- JpaRepository<User, Integer>, boolean existsByUsername(String username)

UserRequest.java
- record JoinDTO(username, password, email)
- username, password: @NotBlank, @Size(min = 4, max = 20) 각 문구
- email: @NotBlank, @Email 문구
- toEntity(String encPassword): User.builder()

UserResponse.java
- record DTO(id, username, email), User를 받는 생성자
- record AvailableDTO(boolean available)

UserService.java
- @Transactional(readOnly = true) 클래스
- @Transactional 회원가입(JoinDTO): 아이디 있으면 BadRequestException, encode 후 save, DTO
- 유저네임중복체크(username): AvailableDTO(!existsByUsername)

UserController.java
- POST /join: @Valid @RequestBody JoinDTO
- GET /check-username: @RequestParam("username")

BadRequestException.java
- NotFoundException과 같은 모양

GlobalExceptionHandler.java
- MethodArgumentNotValidException: 첫 FieldError의 문구만, 400
- BadRequestException: 400, 예외 메시지
- MissingServletRequestParameterException: 400, 이름 + ":값을 입력해주세요"

## 핵심 코드
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

http.csrf(csrf -> csrf.disable());
http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

@NotBlank(message = "비밀번호를 입력해주세요")
@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
String password

String encPassword = passwordEncoder.encode(reqDTO.password());

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
    return Resp.fail(HttpStatus.BAD_REQUEST, e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage());
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | build.gradle, SecurityConfig.java |
| S2 | User.java, UserRepository.java, UserRequest.java, UserResponse.java, UserService.java, UserController.java |
| S3 | SecurityConfig.java, UserService.java |
| S4 | build.gradle, UserRequest.java, UserController.java, GlobalExceptionHandler.java |
| S5 | BadRequestException.java, UserRepository.java, UserService.java, GlobalExceptionHandler.java |
| S6 | UserResponse.java, UserService.java, UserController.java |
| S7 | GlobalExceptionHandler.java |

## 테스트 확인 방법
- S1 GET /api/boards, /api/boards/1, /h2-console이 200
- S2 POST /join이 200, body에 id, username, email
- S3 DB의 password가 1234가 아니고 $2로 시작한다
- S4 비밀번호 12, 이메일 형식 틀림이 400과 문구
- S5 ssar로 가입하면 400 이미 존재하는 유저네임입니다
- S6 ssar은 available false, 없는 아이디는 true
- S7 username 없이 부르면 400 username:값을 입력해주세요
- 가입한 회원은 테스트마다 지운다. board-list S1의 회원 2명 확인을 지키기 위해서다

## 라이브러리
- spring-boot-starter-security, spring-boot-starter-validation (contract.md의 Spring Security, Validation)

## 다른 장 파일
- server-spring/build.gradle: project-setup
- server-spring/src/main/java/com/newtenco/blog/user/User.java: board-list
- server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java: board-detail
