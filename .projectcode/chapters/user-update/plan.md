# user-update 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/user/User.java | 고침 | update(email, password) |
| server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java | 고침 | 수정 요청 UpdateDTO와 조건부 검사 |
| server-spring/src/main/java/com/newtenco/blog/user/UserService.java | 고침 | 회원수정 |
| server-spring/src/main/java/com/newtenco/blog/user/UserController.java | 고침 | PUT /api/users/me |

## 코드 계획
User.java
- update(String email, String password): 두 값만 바꾼다. 트랜잭션 끝에 변경 감지로 반영된다

UserRequest.java
- record UpdateDTO(email, password)
- email: @NotBlank("이메일을 입력해주세요"), @Email("이메일 형식이 올바르지 않습니다")
- password: @Pattern(regexp = "^$|^.{4,20}$", message = "비밀번호는 4~20자여야 합니다"). 빈 글자는 통과, 글자가 있으면 4~20자
- isPasswordChange(): password가 null이 아니고 비어 있지 않으면 true

UserService.java
- @Transactional 회원수정(UpdateDTO, sessionUserId): findById, 없으면 NotFoundException("회원을 찾을 수 없습니다")
- 비밀번호를 바꾸면 encode, 아니면 기존 password를 그대로 쓴다. user.update 후 DTO

UserController.java
- PUT /api/users/me: @AuthenticationPrincipal User sessionUser, @Valid @RequestBody UpdateDTO

## 핵심 코드
@Pattern(regexp = "^$|^.{4,20}$", message = "비밀번호는 4~20자여야 합니다")
String password

String encPassword = reqDTO.isPasswordChange()
        ? passwordEncoder.encode(reqDTO.password())
        : user.getPassword();
user.update(reqDTO.email(), encPassword);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | User.java, UserRequest.java, UserService.java, UserController.java |
| S2 | UserRequest.java, UserService.java |
| S3 | UserService.java |
| S4 | UserRequest.java, UserController.java |
| S5 | UserRequest.java, UserController.java |
| S6 | UserController.java (jwt-auth의 /api/** 규칙) |

## 테스트 확인 방법
- S1 ssar 토큰으로 PUT이 200, body에 id 1, ssar, 새 이메일
- S2 빈 비밀번호로 고친 뒤 1234로 로그인 200
- S3 5678로 고친 뒤 5678 로그인 200, 1234 로그인 401, DB 값이 $2로 시작
- S4 비밀번호 12가 400과 문구
- S5 이메일 빈 값과 형식 틀림이 400과 문구
- S6 토큰 없이 PUT이 401
- 테스트마다 ssar의 이메일과 비밀번호를 data.sql 값으로 되돌린다

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/user/User.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java: user-join
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java: user-join
