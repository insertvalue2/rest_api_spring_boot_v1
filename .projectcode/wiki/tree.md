# 폴더 구조

기준: board-owner 장 끝

```
gradle/
  wrapper/
    gradle-wrapper.jar
    gradle-wrapper.properties
server-spring/
  gradle/
    wrapper/
      gradle-wrapper.jar
      gradle-wrapper.properties
  src/
    main/
      java/
        com/
          newtenco/
            blog/
              board/
                Board.java
                BoardController.java   고침
                BoardRepository.java
                BoardRequest.java
                BoardResponse.java   고침
                BoardService.java   고침
              core/
                filter/
                  JwtAuthorizationFilter.java
                handler/
                  errors/
                    BadRequestException.java
                    ForbiddenException.java
                    NotFoundException.java
                    UnauthorizedException.java
                  GlobalExceptionHandler.java
                util/
                  JwtUtil.java
                  Resp.java
                  RespFilter.java
                SecurityConfig.java
              reply/
                Reply.java
                ReplyController.java
                ReplyRepository.java
                ReplyRequest.java
                ReplyResponse.java
                ReplyService.java
              user/
                User.java
                UserController.java
                UserRepository.java
                UserRequest.java
                UserResponse.java
                UserService.java
              BlogApplication.java
      resources/
        db/
          data.sql
        application.properties
  .gitignore
  build.gradle
  gradlew
  gradlew.bat
  settings.gradle
gradlew
gradlew.bat
settings.gradle
```
