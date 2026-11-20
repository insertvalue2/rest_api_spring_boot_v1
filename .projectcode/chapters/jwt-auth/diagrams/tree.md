# 폴더 구조

비교 기준: done/user-login. 새로 생긴 파일과 고친 파일을 표시했다.

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
                BoardController.java
                BoardRepository.java
                BoardResponse.java
                BoardService.java
              core/
                filter/
                  JwtAuthorizationFilter.java   새로
                handler/
                  errors/
                    BadRequestException.java
                    NotFoundException.java
                    UnauthorizedException.java
                  GlobalExceptionHandler.java
                util/
                  JwtUtil.java   고침
                  Resp.java
                  RespFilter.java   새로
                SecurityConfig.java   고침
              user/
                User.java
                UserController.java   고침
                UserRepository.java
                UserRequest.java
                UserResponse.java
                UserService.java   고침
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

