# 시나리오별 구조도

## 서버 실행과 H2 콘솔 접속

```mermaid
flowchart LR
  A[gradlew bootRun] --> B[BlogApplication]
  B --> C[(H2 jdbc:h2:mem:test)]
  B --> D[GET /h2-console]
  D --> C
```

만든 장: project-setup
맡는 파일
- server-spring/build.gradle
- server-spring/src/main/java/com/newtenco/blog/BlogApplication.java
- server-spring/src/main/resources/application.properties

## 글 목록 보기와 검색

```mermaid
flowchart LR
  A[GET /api/boards?page&keyword] --> B[BoardController]
  B --> C[BoardService]
  C --> D[BoardRepository]
  D --> E[(board_tb)]
  D --> F[(user_tb)]
  C --> G[BoardResponse.PageDTO]
  G --> H[BoardResponse.ListDTO]
  B --> I[Resp]
```

만든 장: board-list, board-paging
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
- server-spring/src/main/java/com/newtenco/blog/board/Board.java
- server-spring/src/main/java/com/newtenco/blog/user/User.java
- server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java
- server-spring/src/main/resources/db/data.sql

## 글 상세 보기

```mermaid
flowchart LR
  A[GET /api/boards/id] --> B[BoardController]
  B --> C[BoardService]
  C --> D[BoardRepository]
  D --> E[(board_tb)]
  D --> F[(user_tb)]
  C --> G[BoardResponse.DetailDTO]
  C --> H[NotFoundException]
  H --> I[GlobalExceptionHandler]
  B --> J[Resp]
  I --> J
```

만든 장: board-detail
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java
- server-spring/src/main/java/com/newtenco/blog/core/handler/errors/NotFoundException.java
- server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
- server-spring/src/main/java/com/newtenco/blog/core/util/Resp.java

## 회원가입

```mermaid
flowchart LR
  A[POST /join] --> B[UserController]
  B --> C[UserService]
  C --> D[PasswordEncoder]
  C --> E[UserRepository]
  E --> F[(user_tb)]
  C --> G[BadRequestException]
  G --> H[GlobalExceptionHandler]
  B --> I[Resp]
```

만든 장: user-join
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java
- server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
- server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
- server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
- server-spring/src/main/java/com/newtenco/blog/user/User.java
- server-spring/src/main/java/com/newtenco/blog/core/handler/errors/BadRequestException.java
- server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

## 아이디 중복 확인

```mermaid
flowchart LR
  A[GET /check-username] --> B[UserController]
  B --> C[UserService]
  C --> D[UserRepository]
  D --> E[(user_tb)]
  C --> F[UserResponse.AvailableDTO]
```

만든 장: user-join
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java
- server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
- server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java

## 로그인

```mermaid
flowchart LR
  A[POST /login] --> B[UserController]
  B --> C[UserService]
  C --> D[UserRepository]
  D --> E[(user_tb)]
  C --> F[PasswordEncoder]
  C --> G[JwtUtil]
  C --> H[UnauthorizedException]
  H --> I[GlobalExceptionHandler]
  B --> J[Authorization 헤더]
```

만든 장: user-login
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java
- server-spring/src/main/java/com/newtenco/blog/user/UserRepository.java
- server-spring/src/main/java/com/newtenco/blog/user/UserRequest.java
- server-spring/src/main/java/com/newtenco/blog/user/UserResponse.java
- server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
- server-spring/src/main/java/com/newtenco/blog/core/handler/errors/UnauthorizedException.java
- server-spring/src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
- server-spring/src/main/resources/application.properties

## 내 정보 보기 (토큰 인증)

```mermaid
flowchart LR
  A[GET /api/users/me] --> B[JwtAuthorizationFilter]
  B --> C[JwtUtil.verify]
  B --> D[SecurityConfig URL 규칙]
  D --> E[UserController]
  D --> F[RespFilter 401 403]
  E --> G[UserService]
  G --> H[UserRepository]
  H --> I[(user_tb)]
```

만든 장: jwt-auth
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java
- server-spring/src/main/java/com/newtenco/blog/core/util/JwtUtil.java
- server-spring/src/main/java/com/newtenco/blog/core/util/RespFilter.java
- server-spring/src/main/java/com/newtenco/blog/core/SecurityConfig.java
- server-spring/src/main/java/com/newtenco/blog/user/UserController.java
- server-spring/src/main/java/com/newtenco/blog/user/UserService.java

## 글쓰기

```mermaid
flowchart LR
  A[POST /api/boards] --> B[JwtAuthorizationFilter]
  B --> C[BoardController]
  C --> D[BoardRequest.SaveDTO]
  C --> E[BoardService]
  E --> F[BoardRepository]
  F --> G[(board_tb)]
  E --> H[BoardResponse.ListDTO]
```

만든 장: board-write
맡는 파일
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java
- server-spring/src/main/java/com/newtenco/blog/board/BoardRequest.java
- server-spring/src/main/java/com/newtenco/blog/board/Board.java
