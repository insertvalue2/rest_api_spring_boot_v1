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
