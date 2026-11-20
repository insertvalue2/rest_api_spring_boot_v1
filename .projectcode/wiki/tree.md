# 폴더 구조

기준: board-update-delete 장 끝

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
                Board.java   고침
                BoardController.java   고침
                BoardRepository.java
                BoardRequest.java   고침
                BoardResponse.java
                BoardService.java   고침
              core/
                filter/
                  JwtAuthorizationFilter.java
                handler/
                  errors/
                    BadRequestException.java
                    ForbiddenException.java   새로
                    NotFoundException.java
                    UnauthorizedException.java
                  GlobalExceptionHandler.java   고침
                util/
                  JwtUtil.java
                  Resp.java
                  RespFilter.java
                SecurityConfig.java   고침
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
